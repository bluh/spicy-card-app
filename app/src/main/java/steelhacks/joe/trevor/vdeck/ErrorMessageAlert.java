package steelhacks.joe.trevor.vdeck;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ErrorMessageAlert {
    public static AlertDialog create(Context context, String error, DialogInterface.OnClickListener listener){
        return (new AlertDialog.Builder(context))
                .setTitle("Error")
                .setMessage("An error has occurred: \n" + error)
                .setPositiveButton("Exit", listener)
                .create();
    }

    public static AlertDialog create(Context context, String error){
        return (new AlertDialog.Builder(context))
                .setTitle("Error")
                .setMessage("An error has occurred: \n" + error)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }
}
