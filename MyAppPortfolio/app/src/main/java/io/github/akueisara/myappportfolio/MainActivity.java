package io.github.akueisara.myappportfolio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.button;
import static android.R.attr.duration;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.button_1)
    Button mButton1;
    @BindView(R.id.button_2)
    Button mButton2;
    @BindView(R.id.button_3)
    Button mButton3;
    @BindView(R.id.button_4)
    Button mButton4;
    @BindView(R.id.button_5)
    Button mButton5;
    @BindView(R.id.button_6)
    Button mButton6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_text_for_pop_movies), Toast.LENGTH_SHORT).show();
            }
        });
        mButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_text_for_stock_hawk), Toast.LENGTH_SHORT).show();
            }
        });
        mButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_text_for_build_bigger), Toast.LENGTH_SHORT).show();
            }
        });
        mButton4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_text_for_make_app_material), Toast.LENGTH_SHORT).show();
            }
        });
        mButton5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_text_for_go_ubiquitous), Toast.LENGTH_SHORT).show();
            }
        });
        mButton6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_text_for_capstone), Toast.LENGTH_SHORT).show();
            }
        });



    }
}
