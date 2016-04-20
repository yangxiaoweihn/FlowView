package ws.dyt.flowviewtest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Random;

import ws.dyt.view.FlowView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
    }

    private void init(){
        initFollowView((FlowView) findViewById(R.id.flowView), R.layout.item_flow);
        initFollowView((FlowView) findViewById(R.id.flowView_zise), R.layout.item_flow_zise);
        initFollowView((FlowView) findViewById(R.id.flowView_red), R.layout.item_flow_red);
    }

    private void initFollowView(final FlowView flowView, final int itemLayoutResId){
        flowView.setAdapter(new FlowView.Adapter() {
            @Override
            public FlowView.ViewHolder create() {
                return new FlowView.ViewHolder(LayoutInflater.from(MainActivity.this).inflate(itemLayoutResId, null)) {
                };
            }

            @Override
            public void bind(FlowView.ViewHolder viewHolder, int position) {
                TextView tv = null;
                if (R.layout.item_flow == itemLayoutResId) {
                    tv = (TextView) viewHolder.itemView;
                } else if (R.layout.item_flow_zise == itemLayoutResId) {
                    tv = (TextView) viewHolder.itemView.findViewById(R.id.tvFlowView);
                }else if (R.layout.item_flow_red == itemLayoutResId) {
                    tv = (TextView) viewHolder.itemView;
                }
                tv.setText((String) flowView.getItem(position));
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
        }).addDataOnly(Arrays.asList(getResources().getStringArray(R.array.item_ch)));
        flowView.showNextPage();

        final String data = "1234567890-=!@#$%^&*()_+qwertyuiopasdfghjkl;'zxcvbnm,./[]QWERTYUIOP{}|ASDFGHJKL:ZXCVBNM<>?";
        final Random random = new Random();
        final int l = data.length();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int a = random.nextInt(l);
                final int b = Math.abs(a - random.nextInt(13));
                final int c = a >= b ? a : b;
                flowView.addData(b == c ? "#=#" : new String(data.substring(b, c)));
            }
        });
    }

}
