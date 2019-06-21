package com.example.game2048;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public static final String SP_KEY_BEST_SCORE = "bestScore";
    private  SoundPool soundPool=new SoundPool(10,AudioManager.STREAM_MUSIC,5);
    private HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    private int score = 0;
    private TextView tvScore,tvBestScore;
    private LinearLayout rootLayout = null;
    private Button btnNewGame;
    private GameView gameView;
    private AnimLayer animLayer = null;

    private boolean mapFlag=false;

    private static MainActivity mainActivity = null;


    public MainActivity(){
        mainActivity=this;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootLayout=findViewById(R.id.container);
        rootLayout.setBackgroundColor(0xfffaf8ef);
        tvScore=findViewById(R.id.tvScore);
        tvBestScore = (TextView) findViewById(R.id.tvBestScore);
        gameView = (GameView) findViewById(R.id.gameView);
        btnNewGame = (Button) findViewById(R.id.btnNewGame);
        btnNewGame.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) {
            gameView.startGame();
        }});
        animLayer = (AnimLayer) findViewById(R.id.animLayer);

        soundMap.put(1,soundPool.load(this,R.raw.game_music,1));//加载音效

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener(){

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (sampleId==soundMap.size()){
                    mapFlag=true;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void  clearScore(){
        score=0;
        showScore();
    }

    public void addScore(int s){
        score+=s;
        showScore();

        int maxScore = Math.max(score, getBestScore());
        saveBestScore(maxScore);
        showBestScore(maxScore);
    }

    public void saveBestScore(int s){
        SharedPreferences.Editor e = getPreferences(MODE_PRIVATE).edit();
        e.putInt(SP_KEY_BEST_SCORE, s);
        e.commit();
    }

    public int getBestScore(){
        return getPreferences(MODE_PRIVATE).getInt(SP_KEY_BEST_SCORE, 0);
    }
    public void showBestScore(int s){
        tvBestScore.setText(s+"");
    }
    public AnimLayer getAnimLayer() {
        return animLayer;
    }


    public void showScore(){
        tvScore.setText(score+"");
    }

public  static MainActivity getMainActivity(){
        return mainActivity;
}

    @Override
    protected void onStart() {
        System.out.println("onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        System.out.println("onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        System.out.println("onRestart");
        super.onRestart();
    }

    @Override
    protected void onPause() {
        System.out.println("onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        System.out.println("onResume");
        super.onResume();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

        } else if (!requestPermissions()) {
            return;
        }

        if (permissionsDialog != null) {
            permissionsDialog.dismiss();
            permissionsDialog = null;
        }
    }

    /**
     * 6.0以上申请权限
     */
    private boolean requestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {Manifest.permission.READ_PHONE_STATE};
            for (String str : permissions) {
                int permission = ContextCompat.checkSelfPermission(this, str);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, 1111);
                    return false;
                }
            }
        }
        return true;
    }
    private AlertDialog permissionsDialog;
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1111:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        if (!shouldShowRequestPermissionRationale(permissions[i])) {

                            if (permissionsDialog != null && permissionsDialog.isShowing()) {
                                return;
                            }
                            showPermissionsDialog();
                            return;
                        } else {
                            requestPermissions();
                        }
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private synchronized void showPermissionsDialog() {
        if (permissionsDialog != null) {
            permissionsDialog.dismiss();
            permissionsDialog = null;
        }
        permissionsDialog = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("提示")
                .setMessage("当前应用缺少必要权限，请于 设置->权限 中打开所需权限")
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                })
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        getApplication().onTerminate();
                    }
                })
                .setCancelable(false)
                .create();
        permissionsDialog.show();
    }

    public SoundPool getSoundPool() {
        return soundPool;
    }


    public HashMap<Integer, Integer> getSoundMap() {
        if (mapFlag){
            return soundMap;
        }
        return null;
    }


}
