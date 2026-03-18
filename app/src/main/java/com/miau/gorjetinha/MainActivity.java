package com.miau.gorjetinha;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText inputValorConta;
    private Button btn10, btn15, btn20, btnCalcularCustom;
    private CheckBox checkCustom;
    private EditText inputCustomPorcentagem;
    private TextView textGorjeta, textTotal;
    private final Locale localeBR = new Locale("pt", "BR");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputValorConta = findViewById(R.id.editTextText);
        btn10 = findViewById(R.id.button4);
        btn15 = findViewById(R.id.button5);
        btn20 = findViewById(R.id.button6);
        btnCalcularCustom = findViewById(R.id.btnCalcularCustom);
        checkCustom = findViewById(R.id.checkBox);
        inputCustomPorcentagem = findViewById(R.id.editTextText3);
        textGorjeta = findViewById(R.id.textView6);
        textTotal = findViewById(R.id.textView7);

        // Máscara de Moeda
        inputValorConta.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    inputValorConta.removeTextChangedListener(this);
                    String cleanString = s.toString().replaceAll("[^\\d]", "");
                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getCurrencyInstance(localeBR).format(parsed / 100);
                        current = formatted;
                        inputValorConta.setText(formatted);
                        inputValorConta.setSelection(formatted.length());
                    } else {
                        current = "";
                        inputValorConta.setText("");
                    }
                    inputValorConta.addTextChangedListener(this);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Lógica para habilitar/desabilitar a porcentagem customizada
        checkCustom.setOnCheckedChangeListener((buttonView, isChecked) -> {
            inputCustomPorcentagem.setEnabled(isChecked);
            btnCalcularCustom.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) {
                inputCustomPorcentagem.setText("");
            }
        });

        // Cliques nos botões fixos
        btn10.setOnClickListener(v -> calcular(10));
        btn15.setOnClickListener(v -> calcular(15));
        btn20.setOnClickListener(v -> calcular(20));

        // Clique no botão de cálculo customizado
        btnCalcularCustom.setOnClickListener(v -> {
            String customStr = inputCustomPorcentagem.getText().toString();
            if (!customStr.isEmpty()) {
                try {
                    double customPct = Double.parseDouble(customStr);
                    calcular(customPct);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Porcentagem inválida", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Digite a porcentagem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calcular(double porcentagem) {
        String valorStr = inputValorConta.getText().toString();

        if (valorStr.isEmpty()) {
            Toast.makeText(this, "Digite o valor da conta", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Limpa e converte o valor da conta
            String cleanString = valorStr.replaceAll("[^\\d]", "");
            double valorConta = Double.parseDouble(cleanString) / 100.0;

            // Cálculos
            double valorGorjeta = valorConta * (porcentagem / 100.0);
            double valorTotal = valorConta + valorGorjeta;

            // Atualiza a tela
            textGorjeta.setText(String.format(localeBR, "Gorjeta (%.1f%%): %s",
                    porcentagem, NumberFormat.getCurrencyInstance(localeBR).format(valorGorjeta)));
            textTotal.setText(String.format(localeBR, "Total: %s",
                    NumberFormat.getCurrencyInstance(localeBR).format(valorTotal)));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Erro no cálculo", Toast.LENGTH_SHORT).show();
        }
    }
}