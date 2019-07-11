package me.gensh.helloustb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.gensh.io.FileInfo;
import me.gensh.io.IOUtils;

public class FileManager extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        Toolbar toolbar = findViewById(R.id.file_manager_toolbar);
        setSupportActionBar(toolbar);
        inti();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
//            case R.id.action_settings:
//                return true;
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void inti() {
        final ArrayList<File> file_list = IOUtils.getFileListInSelfDownloadsDirectory();
        List<Map<String, Object>> List = new ArrayList<>();
        for (int i = 0; i < file_list.size(); i++) {
            Map<String, Object> listitem = new HashMap<>();
            listitem.put("icon", FileInfo.getFileIcon(FileInfo.getEnd(file_list.get(i))));
            listitem.put("name", file_list.get(i).getName());
            listitem.put("time", FileInfo.getFileDate(file_list.get(i)));
            listitem.put("size", FileInfo.getFileSize(file_list.get(i)));
            List.add(listitem);
        }

        ListView li = findViewById(R.id.file_list);
        SimpleAdapter Adapter = new SimpleAdapter(this, List,
                R.layout.listview_file_manager,
                new String[]{"icon", "name", "time", "size"},
                new int[]{R.id.file_icon, R.id.file_name, R.id.file_time, R.id.file_size});
        li.setAdapter(Adapter);

        li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                FileManager.this.openFile(file_list.get(position));
            }
        });
    }

    private void openFile(File file) {
        String end = FileInfo.getEnd(file);
        String type = FileInfo.matchApp(end);
        if (type != null && !type.isEmpty()) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), type);  // TODO: 2017/9/17
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.noApplicationToOpen, Toast.LENGTH_LONG).show();
        }
    }
}
