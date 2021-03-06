package steelhacks.joe.trevor.vdeck;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Splash extends AppCompatActivity {


    public void playClick(View arg){
        startActivity(new Intent(this, Connect.class));
    }

    public void buildClick(View arg){
        Toast.makeText(this, "¯\\_(ツ)_/¯", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new CountDownTimer(2500, 500){
            public void onFinish(){
                setContentView(R.layout.activity_main_menu);
            }

            public void onTick(long tick){
                //etc
            }
        }.start();
    }
}
