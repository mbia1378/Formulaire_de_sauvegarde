package com.hellohasan.sqlite_project.Features.ShowStudentList;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hellohasan.sqlite_project.Database.DatabaseQueryClass;
import com.hellohasan.sqlite_project.Features.CreateStudent.Student;
import com.hellohasan.sqlite_project.Features.CreateStudent.StudentCreateDialogFragment;
import com.hellohasan.sqlite_project.Features.CreateStudent.StudentCreateListener;
import com.hellohasan.sqlite_project.R;
import com.hellohasan.sqlite_project.Util.Config;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class StudentListActivity extends AppCompatActivity implements StudentCreateListener{

    private DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(this);

    private List<Student> studentList = new ArrayList<>();

    private TextView studentListEmptyTextView;
    private RecyclerView recyclerView;
    private StudentListRecyclerViewAdapter studentListRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Logger.addLogAdapter(new AndroidLogAdapter());

        recyclerView = (RecyclerView) findViewById(R.id.studentRecyclerView);
        studentListEmptyTextView = (TextView) findViewById(R.id.emptyStudentListTextView);

        studentList.addAll(databaseQueryClass.getAllStudent());

        studentListRecyclerViewAdapter = new StudentListRecyclerViewAdapter(this, studentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(studentListRecyclerViewAdapter);

        viewVisibility();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStudentCreateDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_delete){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Etes-vous sûr de vouloir supprimer cet enregistrement?");
            alertDialogBuilder.setPositiveButton("Oui",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            boolean isAllDeleted = databaseQueryClass.deleteAllStudents();
                            if(isAllDeleted){
                                studentList.clear();
                                studentListRecyclerViewAdapter.notifyDataSetChanged();
                                viewVisibility();
                            }
                        }
                    });

            alertDialogBuilder.setNegativeButton("Non",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void viewVisibility() {
        if(studentList.isEmpty())
            studentListEmptyTextView.setVisibility(View.VISIBLE);
        else
            studentListEmptyTextView.setVisibility(View.GONE);
    }

    private void openStudentCreateDialog() {
        StudentCreateDialogFragment studentCreateDialogFragment = StudentCreateDialogFragment.newInstance("Créer un planteur", this);
        studentCreateDialogFragment.show(getSupportFragmentManager(), Config.CREATE_STUDENT);
    }

    @Override
    public void onStudentCreated(Student student) {
        studentList.add(student);
        studentListRecyclerViewAdapter.notifyDataSetChanged();
        viewVisibility();
        Logger.d(student.getName());
    }

}
