package br.com.valterdiascalhas.orcamentos;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.support.v7.app.AlertDialog;

public class BackupActivity extends AppCompatActivity {
    //git test 14:53

    private DBAdapter dbAdapter;
    private SQLiteDatabase sqLiteDatabase;
    private TextView tvStatus;
    private TextView tvProgress;
    private TextView tvStatusDropBox;
    private Button btnDropBox;
    private ImageView ivDropBox;
    public static Boolean bBackgroundJobs;
    private ContentValues contentValues;

    private static final String APP_KEY = "vwk5jtt3pjuq7jx";
    private static final String APP_SECRET = "8fk9isxb121ypit";
    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    private static final boolean USE_OAUTH1 = false;
    DropboxAPI<AndroidAuthSession> dropboxAPI;
    private boolean mLoggedIn;
    //Permission type App folder
    private final String DROBOX_APP_DIR = "/";
    private String DATABASE_NAME = "appledore";
    private String FOTOS = "FOTOS/";
    private String DB_FULL_PATH;
    private final String BUTTON_LOG_OUT = "Aperte para Sair do Dropbox";
    private final String BUTTON_LOG_IN = "Aperte para Entrar no Dropbox";
    private final String LABEL_LOG_OUT = "Você está deslogado";
    private final String LABEL_LOG_IN = "Você está logado";

    private List<DropboxAPI.Entry> list;

    public void showMe(View v) {
        dbAdapter = new DBAdapter(getApplicationContext());
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
        L.m(Environment.getExternalStorageDirectory().toString());// /mnt/sdcard
        L.m(Environment.getRootDirectory().getAbsolutePath());// /system
        L.m(Environment.getDataDirectory().getAbsolutePath());// /data
        L.m(Environment.getExternalStorageState());//mounted
        /** If you want to save files that are private to your app,
         * you can acquire the appropriate directory by calling getExternalFilesDir() */
        L.m(getExternalFilesDir(null).toString());///mnt/sdcard/Android/data/br.com.valterdiascalhas.orcamentos/files
        L.m(dbAdapter.mydbHelper.getDatabaseName());//appledore
        L.m(sqLiteDatabase.getPath());///data/data/br.com.valterdiascalhas.orcamentos/databases/appledore

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_and_restore);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarBaR);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Backup e Recuperação");
        getSupportActionBar().setSubtitle("Configurações");

        /** DROPBOX We create a new AuthSession so that we can use the Dropbox API. **/
        AndroidAuthSession session = buildSession();
        dropboxAPI = new DropboxAPI<>(session);
        checkAppKeySetup();
        mLoggedIn = dropboxAPI.getSession().isLinked();
        if (!mLoggedIn) {
            if (USE_OAUTH1) {
                dropboxAPI.getSession().startAuthentication(BackupActivity.this);
            } else {
                dropboxAPI.getSession().startOAuth2Authentication(BackupActivity.this);
            }
        }

        list = new ArrayList<>();
        /** DROPBOX FIM **/

        btnDropBox = (Button) findViewById(R.id.btnDropBox);
        ivDropBox = (ImageView) findViewById(R.id.ivDropBox);
        tvStatusDropBox = (TextView) findViewById(R.id.tvStatusDropBox);

        if (mLoggedIn) {
            btnDropBox.setText(BUTTON_LOG_OUT);
            ivDropBox.setImageResource(R.drawable.dropboxloggedin);
            tvStatusDropBox.setText(LABEL_LOG_IN);
        } else {
            btnDropBox.setText(BUTTON_LOG_IN);
            ivDropBox.setImageResource(R.drawable.dropboxloggedout);
            tvStatusDropBox.setText(LABEL_LOG_OUT);
        }

        dbAdapter = new DBAdapter(getApplicationContext());
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
        DATABASE_NAME = dbAdapter.mydbHelper.getDatabaseName();
        DB_FULL_PATH = sqLiteDatabase.getPath();
        //dbAdapter.mydbHelper.dropTableModeloDeCalha(sqLiteDatabase);
        bBackgroundJobs = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_backup_and_restore, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.mlogDropBox) {
            logDropBox(getCurrentFocus());
            return true;
        }
        if (id == R.id.menviarDB) {
            enviarDB(getCurrentFocus());
            return true;
        }
        if (id == R.id.mcopyDataBaseSdCard) {
            copyDataBaseSdCard(getCurrentFocus());
            return true;
        }
        if (id == R.id.mreceberDB) {
            receberDB(getCurrentFocus());
            return true;
        }
        if (id == R.id.mimportDataBaseSdCard) {
            importDataBaseSdCard(getCurrentFocus());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logDropBox(View V) {
        if (mLoggedIn) {
            logOut();
        } else {
            btnDropBox.setText(BUTTON_LOG_IN);
            tvStatusDropBox.setText(LABEL_LOG_OUT);
            if (USE_OAUTH1) {
                dropboxAPI.getSession().startAuthentication(BackupActivity.this);
            } else {
                dropboxAPI.getSession().startOAuth2Authentication(BackupActivity.this);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /** DROPBOX **/
        AndroidAuthSession session = dropboxAPI.getSession();
        /** The next part must be inserted in the onResume() method of the
         *** activity from which session.startAuthentication() was called, so
         *** that Dropbox authentication completes properly. **/
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();
                // Store it locally in our app for later use
                storeAuth(session);
                setLoggedIn(true);
            } catch (IllegalStateException e) {
                L.t(getApplicationContext(), "Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
            }
        }

    }

    public void enviarDB(View v) {

        if (Utilidades.isOnline((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
            bBackgroundJobs = false;
            if (!bBackgroundJobs) {

                File localDatabaseFile = new File(DB_FULL_PATH);
                String remoteDatabaseString = DROBOX_APP_DIR + DATABASE_NAME;
                UploadToDropBox upload = new UploadToDropBox(this, dropboxAPI, remoteDatabaseString, localDatabaseFile);
                upload.execute();

                Date date = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss", Locale.US);
                remoteDatabaseString = remoteDatabaseString + " " + df.format(date);
                UploadToDropBox upload2 = new UploadToDropBox(this, dropboxAPI, remoteDatabaseString, localDatabaseFile);
                upload2.execute();

                /** L.m(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()); */
                File dir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString());
                File localFiles[] = dir.listFiles();
                for (File f : localFiles) {

                    // L.m(f.getName());
                }
                String remotePath = DROBOX_APP_DIR + FOTOS;
                UploadMultipleToDropBox u = new UploadMultipleToDropBox(this, dropboxAPI, remotePath, localFiles);
                u.execute();

            } else L.t(v.getContext(), "Upload em Andamento");


        } else L.t(v.getContext(), "No Internet");


    }

    public void receberDB(View v) {
        if (Utilidades.isOnline((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(BackupActivity.this);
            builder1.setMessage(R.string.atualizar_banco_question);
            builder1.setCancelable(true);
            builder1.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();

                    File localDatabaseFile = new File(DB_FULL_PATH);
                    String remoteDatabaseString = DROBOX_APP_DIR + DATABASE_NAME;
                    DownloadFromDropbox download = new DownloadFromDropbox(BackupActivity.this, dropboxAPI, remoteDatabaseString, localDatabaseFile);
                    download.execute();


                }
            });
            builder1.setNegativeButton("NÂO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert11 = builder1.create();
            alert11.show();

        } else L.t(v.getContext(), "No Internet");
    }

    public void copyDataBaseSdCard(View v) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                String currentDBPath = DB_FULL_PATH;
                String backupDBPath = DATABASE_NAME;
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                if (currentDB.exists()) {

                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            L.m(e.toString());
        }
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        Uri myUri;
        myUri = Uri.parse("file://" + absolutePath + "/" + DATABASE_NAME);
        Intent myIntent = new Intent(Intent.ACTION_SEND);
        myIntent.setType("*/*");
        myIntent.putExtra(Intent.EXTRA_STREAM, myUri);
        String[] sendTo = {"george.a.1968@gmail.com"};
        myIntent.putExtra(Intent.EXTRA_EMAIL, sendTo);
        myIntent.putExtra(Intent.EXTRA_SUBJECT, "Backup do Banco de Dados");
        myIntent.putExtra(Intent.EXTRA_TEXT, "Banco de Dados anexo \nRegards\nGeorge");
        Intent chooser = Intent.createChooser(myIntent, "Enviando o Banco de Dados");
        startActivity(chooser);
    }

    public void importDataBaseSdCard(View v) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(BackupActivity.this);
        builder1.setMessage("O Banco atual será perdido!\nAtualizar Banco de Dados?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();


                try {
                    File sdcard = Environment.getExternalStorageDirectory();
                    if (sdcard.canWrite()) {
                        String currentDBPath = DB_FULL_PATH;
                        String backupDBPath = DATABASE_NAME;
                        String backupDBPath2 = "Download/" + DATABASE_NAME;
                        File currentDB = new File(currentDBPath);
                        File backupDB = new File(sdcard, backupDBPath);
                        File backupDB2 = new File(sdcard, backupDBPath2);
                        if (backupDB.exists()) {

                            FileChannel src = new FileInputStream(backupDB).getChannel();
                            FileChannel dst = new FileOutputStream(currentDB).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();
                            L.t(getApplicationContext(), "Recuperação efetuade");

                        } else if (backupDB2.exists()) {
                            FileChannel src = new FileInputStream(backupDB2).getChannel();
                            FileChannel dst = new FileOutputStream(currentDB).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();
                            L.t(getApplicationContext(), "Recuperação efetuade");
                        } else

                            L.t(getApplicationContext(), "Recuperação falhou.\n" +
                                    "Verifique se o Arquivo está na raiz ou na pasta 'Download' do Armazenamento Interno");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    L.t(getApplicationContext(), "Recuperação falhou.\n" +
                            "Verifique se o Arquivo está na raiz ou na pasta 'Download' do Armazenamento Interno");
                }


            }
        });
        builder1.setNegativeButton("NÂO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    /**
     * DROPBOX
     **/

    public void downloadDirectoryDropbox(View view) {
        String localPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/";
        String remotePath = "/FOTOS";
        DownloadDirectoryDropbox download = new DownloadDirectoryDropbox(BackupActivity.this, dropboxAPI, localPath, remotePath);
        download.execute();
    }

    public void loadList(View view) {
        new Thread() {
            public void run() {
                try {
                    DropboxAPI.Entry e = dropboxAPI.metadata("/FOTOS", 0, null, true, null);
                    //L.m("ll" + e.fileName());
                    getDocs(e);
                } catch (DropboxException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }.start();
    }

    public void getDocs(DropboxAPI.Entry entry) throws DropboxException {
        List<DropboxAPI.Entry> eList = entry.contents;
        if (eList != null) {
            for (DropboxAPI.Entry e : eList) {
                e = dropboxAPI.metadata(e.path, 0, null, true, null);

            }
        }
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;
        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }

    public void logOut() {
        // Remove credentials from the session
        dropboxAPI.getSession().unlink();
        // Clear our stored keys
        clearKeys();
        // Change UI state to display logged out version
        setLoggedIn(false);
    }

    private void setLoggedIn(boolean loggedIn) {
        mLoggedIn = loggedIn;
        if (loggedIn) {
            btnDropBox.setText(BUTTON_LOG_OUT);
            ivDropBox.setImageResource(R.drawable.dropboxloggedin);
            tvStatusDropBox.setText(LABEL_LOG_IN);
        } else {
            btnDropBox.setText(BUTTON_LOG_IN);
            ivDropBox.setImageResource(R.drawable.dropboxloggedout);
            tvStatusDropBox.setText(LABEL_LOG_OUT);
        }
    }

    private void checkAppKeySetup() {
        // Check to make sure that we have a valid app key
        if (APP_KEY.startsWith("CHANGE") || APP_SECRET.startsWith("CHANGE")) {
            L.t(getApplicationContext(), "Solicitar Chaves no developers.dropbox.com.");
            finish();
            return;
        }
        // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + APP_KEY;
        String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
        //uri="db-1u5s29nmzculms0://1/test"

        testIntent.setData(Uri.parse(uri));
        PackageManager pm = getPackageManager();
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
            L.t(getApplicationContext(), "URL scheme in your app's " +
                    "manifest is not set up correctly. You should have a " +
                    "com.dropbox.client2.android.AuthActivity with the " +
                    "scheme: " + scheme);
            finish();
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
        // Store the OAuth 1 access token, if there is one.  This is only necessary if
        // you're still using OAuth 1.
        AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
        if (oauth1AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.commit();
            return;
        }
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    public void returnMainActivity(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", 7214);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
