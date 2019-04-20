package com.example.voicejournal;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "PostDetailActivity";

    private ListView lv;
    private ListView List_P;
    private ListView List_T;
    private ListView List_C;
    private ListView List_N;
    private Button btn;
    private TextView txt;
    private TextView sms;

    private V_Color VC = new V_Color();
    private String[] word;
    private String[] key = {"давление","температура"};
    private String str;
    private int Number;

    private ArrayList<String> List_Time;
    private ArrayList<String> List_Pressure;
    private ArrayList<String> List_Temperature;
    private ArrayList<String> List_Number;

    private ArrayAdapter<String> Time_a;
    private ArrayAdapter<String> Pressure_a;
    private ArrayAdapter<String> Temperature_a;
    private ArrayAdapter<String> Number_a;

    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Горизонтальная ориентация
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

         mAuth = FirebaseAuth.getInstance();
         mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
         database = FirebaseDatabase.getInstance();
         myRef = database.getReference("Message");
         str = "1";

        //пример записи в БД
        // myRef.setValue("Hello, World!");

        lv = findViewById(R.id.lv);
        btn = findViewById(R.id.btn);
        List_P = findViewById(R.id.List_P);
        List_T = findViewById(R.id.List_T);
        List_C = findViewById(R.id.List_C);
        List_N = findViewById(R.id.List_N);
        txt = findViewById(R.id.textView3);
        sms = findViewById(R.id.SMS);

        List_Time = new ArrayList<>();
        List_Pressure = new ArrayList<>();
        List_Temperature= new ArrayList<>();
        List_Number = new ArrayList<>();

        Time_a = new ArrayAdapter(this, android.R.layout.simple_list_item_1, List_Time);
        Pressure_a = new ArrayAdapter(this, android.R.layout.simple_list_item_1, List_Pressure);
        Temperature_a = new ArrayAdapter(this, android.R.layout.simple_list_item_1, List_Temperature);
        Number_a = new ArrayAdapter(this, android.R.layout.simple_list_item_1, List_Number);


        List_T.setAdapter(Time_a);
        List_C.setAdapter(Temperature_a);
        List_P.setAdapter(Pressure_a);
        List_N.setAdapter(Number_a);

        lv.setFooterDividersEnabled(TRUE);
        List_P.setFooterDividersEnabled(TRUE);
        List_T.setFooterDividersEnabled(TRUE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeak();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this,"onStart",Toast.LENGTH_SHORT).show();
        Toast.makeText(this,"Консоль: "+str,Toast.LENGTH_SHORT).show();



        // Add value event listener to the post
        // [START post_value_event_listener
       myRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               str = (String) dataSnapshot.getValue();

               // Get Post object and use the values to update the UI
               // [END_EXCLUDE]
               // Get Post object and use the values to update the UI
               sms.setText(str);
               // [START_EXCLUDE]

           }
           @Override
           public void onCancelled(DatabaseError databaseError) {
               // Getting Post failed, log a message
               Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
               // [START_EXCLUDE]

           }

       });
        // [END post_value_event_listener]

    }


    private void startSpeak() {
        Intent intent =  new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Запись команды");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
        // Toast.makeText(this,"Начало прослушивания",Toast.LENGTH_SHORT).show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK){
            ArrayList<String> commandList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, commandList));

            for (int i=0; i <=3; i++) {
                if (commandList.contains(VC.color[i])) {
                    lv.setBackgroundColor(VC.Num[i]);
                }
            }

            //Toast.makeText(this,"До этого момента дожили",Toast.LENGTH_SHORT).show();
            // Работа со входной строкой
            String[] text = GetStringArray(commandList);
            myRef.setValue(text[0]);
            // Парсим строки
            word = text[0].split(" ");
            int Numb;
            Numb = word.length;

             for (int j = 0; j <= Numb-1 ; j++) {
                 Toast.makeText(this,word[j],Toast.LENGTH_SHORT).show();
                if (word[j].contains(key[0])){
                    List_Pressure.add( word[j+1]);
                    Number++;
                    List_Number.add(Integer.toString(Number));
                    Pressure_a.notifyDataSetChanged();
                }
                if (word[j].contains(key[1])){
                    List_Temperature.add( word[j+1]);
                    Temperature_a.notifyDataSetChanged();
                }

            }

        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private String[] GetStringArray(ArrayList<String> List) {
        String str[] = new String[List.size()];

        for (int j = 0; j < List.size(); j++) {
            str[j] = List.get(j);
        }
        return str;
    }
}
