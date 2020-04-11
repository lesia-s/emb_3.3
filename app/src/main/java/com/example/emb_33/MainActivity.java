package com.example.emb_33;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText x1Text;
    private EditText x2Text;
    private EditText x3Text;
    private EditText x4Text;
    private EditText yText;

    private TextView aText;
    private TextView bText;
    private TextView cText;
    private TextView dText;

    private Spinner iterationsSpinner;
    private Spinner probabilitySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        x1Text = findViewById(R.id.edit_x1);
        x2Text = findViewById(R.id.edit_x2);
        x3Text = findViewById(R.id.edit_x3);
        x4Text = findViewById(R.id.edit_x4);
        yText = findViewById(R.id.edit_y);

        aText = findViewById(R.id.text_a);
        bText = findViewById(R.id.text_b);
        cText = findViewById(R.id.text_c);
        dText = findViewById(R.id.text_d);

        iterationsSpinner = findViewById(R.id.spIter);
        probabilitySpinner = findViewById(R.id.spProb);

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInput()) {
                    ArrayList<Integer> res = geneticAlgorithhm();
                    aText.setText(Integer.toString(res.get(0)));
                    bText.setText(Integer.toString(res.get(1)));
                    cText.setText(Integer.toString(res.get(2)));
                    dText.setText(Integer.toString(res.get(3)));
                }
            }
        });
    }

    private ArrayList<Integer> geneticAlgorithhm() {
        int x1 = 0, x2 = 0, x3 = 0, x4 = 0, y = 0, iterations = 0;
        double probability = 0.0;
        ArrayList<ArrayList<Integer>> population = new ArrayList<>();

        try {
            x1 = Integer.parseInt(x1Text.getText().toString());
            x2 = Integer.parseInt(x2Text.getText().toString());
            x3 = Integer.parseInt(x3Text.getText().toString());
            x4 = Integer.parseInt(x4Text.getText().toString());
            y = Integer.parseInt(yText.getText().toString());
            iterations = Integer.parseInt(iterationsSpinner.getSelectedItem().toString());
            probability = Double.parseDouble(probabilitySpinner.getSelectedItem().toString());
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            showError("Incorrect input!");
        }

        for (int i = 0; i < 4; i++) {
            population.add(new ArrayList<Integer>());
            for  (int j = 0; j < 4; j++) {
                population.get(i).add(1 + (int) (Math.random() * (y / 2)));
            }
        }

        for (int i = 0; i < iterations; i++) {
            int[] delta = new int[population.size()];
            double allDelta = 0;

            for (int j = 0; j < population.size(); j++) {
                int tempY = population.get(j).get(0) * x1 + population.get(j).get(1) * x2 + population.get(j).get(2) *x3 + population.get(j).get(3) * x4;
                delta[j] = Math.abs(y - tempY);
                if (delta[j] == 0) {
                    return population.get(j);
                }
                allDelta += 1.0/delta[j];
            }

            double[] percent = new double[4];
            for (int j = 0; j < delta.length; j++) {
                percent[j] = 1.0/delta[j]/allDelta;
            }

            double[] range = new double[5];
            range[0] = 0.0;
            range[4] = 100.0;
            for (int j = 0; j < range.length - 1; j ++) {
                range[j + 1] = range[j] + (int)(percent[j] * 100);
            }

            int[] parents = new int[4];
            for (int j = 0; j < parents.length; j++) {
                int rand = (int) (Math.random() * 100);
                for (int k = 0; k < range.length - 1; k++) {
                    if (rand < range[k + 1] && rand >= range[k]) {
                        parents[j] = k;
                    }
                }
            }

            ArrayList<ArrayList<Integer>> children = new ArrayList<>();
            for (int j = 0; j < population.size(); j++) {
                int parent1 = parents[(int) (Math.random() * (parents.length - 1))];
                int parent2 = parents[(int) (Math.random() * (parents.length - 1))];
                int threshold = 1 + (int) (Math.random() * 3);
                children.add(new ArrayList<Integer>());
                for (int k = 0; k < population.get(j).size(); k++) {
                    int gene = (k < threshold) ? population.get(parent1).get(k) : population.get(parent2).get(k);
                    children.get(j).add(gene);
                }

                if (Math.random() < probability) {
                    int choice = (int) (Math.random() * children.get(j).size());
                    int rand = 1 + (int) (Math.random() * (y / 2));
                    children.get(j).set(choice, rand);
                }
            }
            population = children;
        }
        int maxDelta = Integer.MAX_VALUE;
        int i = -1;
        for (int j = 0; j < population.size(); j++) {
            int tempY = population.get(j).get(0) * x1 + population.get(j).get(1) * x2 + population.get(j).get(2) *x3 + population.get(j).get(3) * x4;
            if (tempY - y < maxDelta) {
                maxDelta = tempY;
                i = j;
            }
        }
        Toast.makeText(
                MainActivity.this, "Max iterations reached!", Toast.LENGTH_LONG
        ).show();
        return population.get(i);
}

    private boolean checkInput() {
        if (x1Text.getText().length() == 0 || x2Text.getText().length() == 0
                || x3Text.getText().length() == 0 || x4Text.getText().length() == 0
                || yText.getText().length() == 0) {
            return showError("Empty field!");
        }
        return true;
    }

    private boolean showError(String errorMessage) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Error message")
                .setMessage(errorMessage)
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        return false;
    }

}
