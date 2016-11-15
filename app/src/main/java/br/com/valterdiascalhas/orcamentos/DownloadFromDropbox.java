package br.com.valterdiascalhas.orcamentos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class DownloadFromDropbox extends AsyncTask<Void, Long, Boolean> {

    private DropboxAPI<?> mApi;
    private String mRemoteDatabaseString;
    private File mLocalDatabaseFile;
    private Long mFileLength;
    private Context context;
    private final ProgressDialog progressDialog;
    private String mErrorMsg;
    private Context context2;

    public DownloadFromDropbox(Context context, DropboxAPI<?> api, String remoteDatabaseString, File localDatabaseFile) {
        this.context = context.getApplicationContext();
        mApi = api;
        mRemoteDatabaseString = remoteDatabaseString;
        mLocalDatabaseFile = localDatabaseFile;
        /** For the next progressdialog **************/
        context2 = context;

        progressDialog = new ProgressDialog(context);
        progressDialog.setMax(100);
        progressDialog.setMessage("Recebendo " + localDatabaseFile.getName());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Entry existingEntry = mApi.metadata(mRemoteDatabaseString, 1, null, false, null);
            mFileLength = existingEntry.bytes;
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(mLocalDatabaseFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            DropboxAPI.DropboxFileInfo info = mApi.getFile(mRemoteDatabaseString, null, outputStream, new ProgressListener() {
                @Override
                public long progressInterval() {
                    // Update the progress bar every x ms
                    return 100;
                }

                @Override
                public void onProgress(long bytes, long total) {
                    publishProgress(bytes);
                }
            });
            return true;

        } catch (DropboxUnlinkedException e) {
            mErrorMsg = "Aplicativo não foi authenticado.";
        } catch (DropboxPartialFileException e) {
            // We canceled the operation
            mErrorMsg = "Download canceled";
        } catch (DropboxServerException e) {
            // Server-side exception.  These are examples of what could happen,
            // but we don't do anything special with them here.
            if (e.error == DropboxServerException._304_NOT_MODIFIED) {
                // won't happen since we don't pass in revision with metadata
            } else if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                // Unauthorized, so we should unlink them.  You may want to
                // automatically log the user out in this case.
            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                // Not allowed to access this
            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                // path not found (or if it was the thumbnail, can't be thumbnailed)
            } else if (e.error == DropboxServerException._406_NOT_ACCEPTABLE) {
                // too many entries to return
            } else if (e.error == DropboxServerException._415_UNSUPPORTED_MEDIA) {
                // can't be thumbnailed
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
            mErrorMsg = "Network error.  Try again.";
        } catch (DropboxParseException e) {
            // Probably due to Dropbox server restarting, should retry
            mErrorMsg = "Dropbox error.  Try again.";
        } catch (DropboxException e) {
            // Unknown error
            mErrorMsg = "Unknown error.  Try again.";
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Long... progress) {
        int percent = (int) (100.0 * (double) progress[0] / mFileLength + 0.5);
        progressDialog.setProgress(percent);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        progressDialog.dismiss();
        if (result) {
            showToast("Arquivo recebido do Dropbox");
            String localPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/";
            String remotePath = "/FOTOS";
            DownloadDirectoryDropbox download = new DownloadDirectoryDropbox(context2, mApi,localPath,remotePath);
            download.execute();
        } else {
            // Couldn't download it, so show an error
            showToast(mErrorMsg);
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        error.show();
    }
}

