package steelhacks.joe.trevor.vdeck;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainGame extends AppCompatActivity {

    public void shrugClick(View arg){
        Toast.makeText(this, "¯\\_(ツ)_/¯", Toast.LENGTH_SHORT).show();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
    }
}
