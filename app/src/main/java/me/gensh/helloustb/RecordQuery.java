package me.gensh.helloustb;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import me.gensh.utils.SerializableList;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class RecordQuery extends AppCompatActivity {
    int scoreSum_elective[] = {0, 0};
    int scoreSum[] = {0, 0};//学分相关
    StringBuilder shareScore = new StringBuilder("【晒晒成绩单】这是我这学期的成绩：\n");
    String GPA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_query);
        Toolbar toolbar = (Toolbar) findViewById(R.id.record_query_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle score_boundle = getIntent().getExtras();
        List<Map<String, Object>> score_data = ((SerializableList) score_boundle.get("score_data")).getList();
        GPA = score_boundle.getString("GPA");

        for (Map<String, Object> m : score_data) {
            m.put("score_bg", getScoreColor((String) m.get("myscore")));
            addWeigth((String) m.get("learn_time"), (String) m.get("myscore"), (String) m.get("course_class"));
            m.put("learn_time", m.get("learn_time") + "学分");
            shareScore.append(m.get("course")).append(":").append(m.get("myscore")).append("分\n");
        }
        setListView(score_data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.record_query, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.collect:
                break;
            case R.id.weight:
                String weight_m = "GPA:"+GPA+"\n含选修:" + getWeight(scoreSum_elective) + "\n不含选修:" + getWeight(scoreSum);
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_score_weight)
                        .setMessage(weight_m)
                        .setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.share_score:
                String weight_msg = "加权成绩如下:\n含选修:" + getWeight(scoreSum_elective)
                        + "不含选修:" + getWeight(scoreSum)+"\nGPA:"+GPA;
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareScore.toString() + "\n" + weight_msg);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setListView(List<Map<String, Object>> score) {
        ListView li = (ListView) findViewById(R.id.record_query_list);
        SimpleAdapter Adapter = new SimpleAdapter(this, score,
                R.layout.listview_record_query,
                new String[]{"course", "learn_time", "score_bg", "myscore", "course_class"},
                new int[]{R.id.Course, R.id.LearnTime, R.id.score_bg_image, R.id.MyScore, R.id.CourseClass});
        li.setAdapter(Adapter);
    }

    int[] score_color = {R.color.red, R.color.yellow, R.color.green, R.color.side_nav_bar, R.color.purple};
    private int getScoreColor(String score) {
        if (!isNumeric(score)) {
            return score_color[0];
        }
        int score_int = Integer.parseInt(score);
        if (score_int >= 90) {
            return score_color[4];
        } else if (score_int >= 80) {
            return score_color[3];
        } else if (score_int >= 70) {
            return score_color[2];
        } else if (score_int >= 60) {
            return score_color[1];
        }
        return score_color[0];
    }

    private void addWeigth(String credit, String myscore, String classes) {
        if (isNumeric(credit) && isNumeric(myscore))    //有些成绩就是“通过”
        {
            scoreSum_elective[0] += Integer.parseInt(credit);    //0是学分之和，1是分数累加和
            scoreSum_elective[1] += (Integer.parseInt(myscore) * Integer.parseInt(credit));
            if (!classes.contains("选"))    //必修课
            {
                scoreSum[0] += Integer.parseInt(credit);
                scoreSum[1] += (Integer.parseInt(myscore) * Integer.parseInt(credit));
            }
        }
    }

    public static boolean isNumeric(String str) {
        // TODO 自动生成的方法存根
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private String getWeight(int weight[]) {    //计算加权,返回加权分字符串
        // TODO 自动生成的方法存根
        float score = (float) weight[1] / (float) weight[0];
        DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        return decimalFormat.format(score);//format 返回的是字符串
    }

}
