package br.com.valterdiascalhas.orcamentos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.dropbox.client2.DropboxAPI;
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
import java.util.List;

public class DownloadDirectoryDropbox extends AsyncTask<Void, Long, Boolean> {
    private DropboxAPI<?> dropboxAPI;
    private Context context;
    private String mErrorMsg;
    private String localPath;
    private String remotePath;
    private ProgressDialog progressDialog;

    public DownloadDirectoryDropbox(Context context, DropboxAPI<?> api, String localPath, String remotePath) {
        this.context = context.getApplicationContext();
        dropboxAPI = api;
        this.localPath = localPath;
        this.remotePath = remotePath;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Aguarde Download de Imagens");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            DropboxAPI.Entry entry = dropboxAPI.metadata(remotePath, 0, null, true, null);
            List<DropboxAPI.Entry> eList = entry.contents;
            if (eList != null) {
                for (DropboxAPI.Entry e : eList) {
                    e = dropboxAPI.metadata(e.path, 0, null, true, null);
                    File fileName  = new File(localPath + e.fileName());
                    FileOutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(fileName);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    DropboxAPI.DropboxFileInfo info = dropboxAPI.getFile(e.path, null, outputStream, null);
                }
            }
            return true;

        } catch (DropboxUnlinkedException e) {
            L.m("The AuthSession wasn't properly authenticated or user unlinked.");
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
    protected void onPostExecute(Boolean result) {
        if (result) {
            showToast("Arquivos recebidos do Dropbox");
            progressDialog.dismiss();
        } else {
            // Couldn't download it, so show an error
            L.m(mErrorMsg);
            progressDialog.dismiss();
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        error.show();
    }
}

