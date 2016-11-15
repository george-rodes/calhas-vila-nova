package br.com.valterdiascalhas.orcamentos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.widget.Toast;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

public class UploadToDropBox extends AsyncTask<String, Long, Boolean>{

    private DropboxAPI<?> mApi;
    private String mRemoteDatabaseString;
    private File mLocalDatabaseFile;
    private long mFileLength;
    private UploadRequest mRequest;
    private Context context;
    private final ProgressDialog progressDialog;
    private String mErrorMsg;

    public UploadToDropBox(Context context, DropboxAPI<?> api, String remoteDatabaseString,File localDatabaseFile) {

        // We set the context this way so we don't accidentally leak activities
        this.context = context.getApplicationContext();
        mFileLength = localDatabaseFile.length();
        mApi = api;
        mRemoteDatabaseString = remoteDatabaseString;
        mLocalDatabaseFile = localDatabaseFile;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMax(100);
        progressDialog.setMessage("Enviando " + localDatabaseFile.getName());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // This will cancel the putFile operation
                mRequest.abort();
            }
        });
        progressDialog.show();
    }

    @Override
    protected void onPreExecute() {
      BackupActivity.bBackgroundJobs = true;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            // By creating a request, we get a handle to the putFile operation,
            // so we can cancel it later if we want to
            FileInputStream fis = new FileInputStream(mLocalDatabaseFile);
            mRequest = mApi.putFileOverwriteRequest(mRemoteDatabaseString, fis, mLocalDatabaseFile.length(),
                    new ProgressListener() {
                        @Override
                        public long progressInterval() {
                            // Update the progress bar every x ms
                            return 5;
                        }
                        @Override
                        public void onProgress(long bytes, long total) {
                            publishProgress(bytes);
                        }
                    });

            if (mRequest != null) {
                mRequest.upload();
                return true;
            }

        } catch (DropboxUnlinkedException e) {
            // This session wasn't authenticated properly or user unlinked
            mErrorMsg = "Aplicativo não foi authenticado.";
        } catch (DropboxFileSizeException e) {
            // File size too big to upload via the API
            mErrorMsg = "Arquivo muito grande";
        } catch (DropboxPartialFileException e) {
            // We canceled the operation
            mErrorMsg = "Envio cancelado";
        } catch (DropboxServerException e) {
            // Server-side exception.  These are examples of what could happen,
            // but we don't do anything special with them here.
            if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                // Unauthorized, so we should unlink them.  You may want to
                // automatically log the user out in this case.
            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                // Not allowed to access this
            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                // path not found (or if it was the thumbnail, can't be thumbnailed)
            } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
                // user is over quota
            } else {
                // Something else
            }
            // This gets the Dropbox error, translated into the user's language
            mErrorMsg = e.body.userError;
            if (mErrorMsg == null) {
                mErrorMsg = e.body.error;
            }
        } catch (DropboxIOException e) {
            // Happens all the time, probably want to retry automatically.
            mErrorMsg = "Sem Conexão.  Tente de novo.";
        } catch (DropboxParseException e) {
            // Probably due to Dropbox server restarting, should retry
            mErrorMsg = "Erro no Dropbox. Tente de novo.";
        } catch (DropboxException e) {
            // Unknown error
            mErrorMsg = "Erro desconhecido. Tente de Novo.";
        } catch (FileNotFoundException e) {
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Long... progress) {
        int percent = (int)(100.0*(double)progress[0]/ mFileLength + 0.5);
        progressDialog.setProgress(percent);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        progressDialog.dismiss();
        BackupActivity.bBackgroundJobs = false;
        if (result) {
            showToast("Arquivo enviado ao Dropbox");
        } else {
            showToast(mErrorMsg);
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        error.show();
    }
}
