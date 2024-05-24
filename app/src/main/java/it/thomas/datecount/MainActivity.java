package it.thomas.datecount;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import it.thomas.datecount.databinding.ActivityMainBinding;
import it.thomas.datecount.databinding.DialogUserNameInputBinding;


public class MainActivity extends AppCompatActivity {
    private static final String MALE = "male";
    private static final String FEMALE = "female";
    private ActivityMainBinding binding;
    private LocalDate selectedDate;
    LocalDate todayDate = LocalDate.now();
    private SharedPreferences sp;
    private static final String SELECTED_DATE = "selected_date";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sp = getSharedPreferences("date_count", MODE_PRIVATE);
        initUI();
        initListeners();
    }


    private void initListeners() {

        binding.tvMale.setOnClickListener(v -> {
            showInputDialog(v);
        });

        binding.tvFemale.setOnClickListener(v -> {
            showInputDialog(v);
        });

        binding.btDate.setOnClickListener(v -> {
            showDatePickerDialog();
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.updateDate(selectedDate.getYear(), selectedDate.getMonthValue()-1, selectedDate.getDayOfMonth());
        datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            Log.d("Date Picker", String.format("%d %d %d", year, month, dayOfMonth));
            selectedDate = LocalDate.of(year, month+1, dayOfMonth);
            Log.d("ABC", String.format("%02d/%02d/%04d", dayOfMonth, month+1, year));
            saveData(SELECTED_DATE, String.format("%02d/%02d/%04d", dayOfMonth, month+1, year));
            updateDate();
        });
        datePickerDialog.show();
    }

    private void updateDate() {
        LocalDate todayDate = LocalDate.now();
        long dateBetween = todayDate.toEpochDay() - selectedDate.toEpochDay();
        String days = dateBetween > 1 ? dateBetween + "Days" : dateBetween + "Day";
        binding.tvCount.setText(days);
        binding.btDate.setText(fromLocalDateToString(selectedDate));
    }

    private void showInputDialog(View view) {
        TextView textView = (TextView) view;
        AlertDialog.Builder inputBuilder = new AlertDialog.Builder(this);
        DialogUserNameInputBinding inputBinding = DialogUserNameInputBinding.inflate(getLayoutInflater());
        AlertDialog inputDialog = inputBuilder.setView(inputBinding.getRoot()).create();
        inputDialog.show();

        class OnDialogClick implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                String name = inputBinding.etName.getText().toString();
                if(textView.getId() == R.id.tvMale) {
                    saveData(MALE, name);
                } else {
                    saveData(FEMALE, name);
                }
                textView.setText(name);
                inputDialog.cancel();
            }
        }

        inputBinding.btConfirm.setOnClickListener(new OnDialogClick());
        inputBinding.btCancel.setOnClickListener(v -> {
            inputDialog.cancel();
        });
    }

    private void initUI() {
        binding.tvMale.setText(read(MALE));
        binding.tvFemale.setText(read(FEMALE));
        selectedDate = fromStringToLocalDate(binding.btDate.getText().toString());
        long dateBetween = todayDate.toEpochDay() - selectedDate.toEpochDay();
        String days = dateBetween > 1 ? dateBetween + "Days" : dateBetween + "Day";
        binding.tvCount.setText(days);
    }

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private LocalDate fromStringToLocalDate(String str) {
        return LocalDate.parse(str, formatter);
    }

    private String fromLocalDateToString(LocalDate localDate) {
        return formatter.format(localDate);
    }

    private void saveData(String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String read(String key) {
        return sp.getString(key, "");
    }

}