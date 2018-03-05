package com.cristhian.apptivities.Utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.cristhian.apptivities.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import io.realm.Realm;
import io.realm.internal.IOException;

/**
 * Created by Cristhian on 28-02-2018.
 */

public class RealmBackupRestore {
    private Context context;
    private File EXPORT_REALM_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private String IMPORT_REALM_FILE_NAME = "default.realm";

    public RealmBackupRestore(Context context){
        this.context = context;
    }

    public void backup() {
        Realm realm;
        realm = Realm.getDefaultInstance();
        try {
            File exportRealmFile;
            exportRealmFile = new File(EXPORT_REALM_PATH, context.getString(R.string.app_name) + ".realm");

            exportRealmFile.delete();

            realm.writeCopyTo(exportRealmFile);
            CustomToast(context, context.getString(R.string.activity_activity_toast_backup), Toast.LENGTH_SHORT);

        } catch (IOException e) {
            e.printStackTrace();
            CustomToast(context, "Error: " + e, Toast.LENGTH_SHORT);
        }

        realm.close();
    }

    public void restore() {
        String restoreFilePath = EXPORT_REALM_PATH + "/" + context.getString(R.string.app_name) + ".realm";

        copyBundledRealmFile(restoreFilePath, IMPORT_REALM_FILE_NAME);
        CustomToast(context, context.getString(R.string.activity_activity_toast_restore), Toast.LENGTH_SHORT);
    }

    private String copyBundledRealmFile(String oldFilePath, String outFileName) {
        try {
            File file = new File(context.getFilesDir(), outFileName);

            FileOutputStream outputStream = new FileOutputStream(file);

            FileInputStream inputStream = new FileInputStream(new File(oldFilePath));

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void CustomToast(Context context, String mensaje, int duracion){
        Toast toast = Toast.makeText(context, mensaje, duracion);
        toast.getView().setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        toast.getView().setPadding(10,10,10,10);
        toast.show();
    }
}
