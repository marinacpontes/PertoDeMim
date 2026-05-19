package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.slider.RangeSlider;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

// Tela principal do aplicativo que exibe o mapa e filtros de busca
public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Componentes de interface: seletores deslizantes (Sliders) e campos de texto
    private RangeSlider sliderPreco, sliderDistancia;
    private EditText editMinPreco, editMaxPreco, editMinDistancia, editMaxDistancia;
    private TextView tvSelectedAvaliacao, tvSelectedCategoria;
    private View panelPreco, panelDistancia;
    private boolean isUpdating = false; // Flag para evitar loops infinitos de atualização

    // Listas para armazenar as opções de filtro marcadas pelo usuário
    private final List<String> selectedAvaliacoes = new ArrayList<>();
    private final List<String> selectedCategorias = new ArrayList<>();

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Coordenadas de Fortaleza, CE
        LatLng fortaleza = new LatLng(-3.7319, -38.5267);

        // Adiciona um marcador em Fortaleza
        googleMap.addMarker(new MarkerOptions().position(fortaleza).title("Fortaleza, Ceará"));

        // Move a câmera para lá com zoom
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fortaleza, 12f));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Define o layout da tela principal
        setContentView(R.layout.activity_home);

        // Inicializa o mapa do Google (usando um Fragment)
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Lógica do menu lateral de filtros (abrir e fechar)
        View btnMenu = findViewById(R.id.btnMenu);
        View filtersContainer = findViewById(R.id.filtersContainer);
        View btnCloseFilters = findViewById(R.id.btnCloseFilters);

        btnMenu.setOnClickListener(v -> filtersContainer.setVisibility(View.VISIBLE));
        btnCloseFilters.setOnClickListener(v -> {
            filtersContainer.setVisibility(View.GONE);
            hideAllPanels(); // Fecha também os sub-painéis de preço/distância
        });

        // Sub-painéis de ajuste fino (Preço e Distância)
        panelPreco = findViewById(R.id.panelPreco);
        panelDistancia = findViewById(R.id.panelDistancia);

        // Alterna a exibição dos painéis quando os botões de filtro são clicados
        findViewById(R.id.btnFiltroPreco).setOnClickListener(v -> togglePanel(panelPreco));
        findViewById(R.id.btnFiltroDistancia).setOnClickListener(v -> togglePanel(panelDistancia));

        tvSelectedAvaliacao = findViewById(R.id.tvSelectedAvaliacao);
        tvSelectedCategoria = findViewById(R.id.tvSelectedCategoria);

        // Abre a janelinha para escolher as estrelas da avaliação
        findViewById(R.id.btnFiltroAvaliacao).setOnClickListener(v -> showMultiSelectDialog("Avaliação", 
                new String[]{"1 estrela", "2 estrelas", "3 estrelas", "4 estrelas", "5 estrelas"}, 
                selectedAvaliacoes, tvSelectedAvaliacao));
        
        // Abre a janelinha para escolher as categorias de serviço
        findViewById(R.id.btnFiltroCategoria).setOnClickListener(v -> showMultiSelectDialog(getString(R.string.categoria), 
                new String[]{getString(R.string.beleza_estetica), getString(R.string.saude), 
                        getString(R.string.alimentacao), getString(R.string.educacao), 
                        getString(R.string.manutencao), getString(R.string.tecnologia), "Outros"}, 
                selectedCategorias, tvSelectedCategoria));

        // Configura a lógica de funcionamento dos filtros de preço e distância
        setupPriceFilter();
        setupDistanceFilter();
        setupHomeSearch();

        // Configuração do menu de navegação inferior (Bottom Navigation)
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_mapa); // Marca o Mapa como selecionado

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_perfil) {
                // Vai para a tela de Perfil
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (id == R.id.nav_chat) {
                // Vai para a lista de Mensagens
                startActivity(new Intent(this, ChatActivity.class));
                return true;
            } else if (id == R.id.nav_vitrine) {
                // Vai para a Vitrine de serviços
                startActivity(new Intent(this, VitrineActivity.class));
                return true;
            }
            return true;
        });
    }

    // Lógica para mostrar um painel e esconder o outro (alternância)
    private void togglePanel(View panel) {
        if (panel.getVisibility() == View.VISIBLE) {
            panel.setVisibility(View.GONE);
        } else {
            hideAllPanels(); // Garante que apenas um painel esteja aberto por vez
            panel.setVisibility(View.VISIBLE);
        }
    }

    // Esconde todos os painéis extras de filtro
    private void hideAllPanels() {
        panelPreco.setVisibility(View.GONE);
        panelDistancia.setVisibility(View.GONE);
    }

    // Cria um diálogo com lista de opções (CheckBox) para filtros de múltipla escolha
    private void showMultiSelectDialog(String title, String[] options, List<String> selectedList, TextView targetTextView) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_filtros_multi, null);
        LinearLayout container = view.findViewById(R.id.llOptionsContainer);
        List<CheckBox> checkBoxes = new ArrayList<>();

        // Cria dinamicamente um CheckBox para cada opção da lista
        for (String option : options) {
            CheckBox cb = new CheckBox(this);
            cb.setText(option);
            cb.setPadding(16, 16, 16, 16);
            cb.setChecked(selectedList.contains(option));
            container.addView(cb);
            checkBoxes.add(cb);
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(view)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Ao clicar OK, salva as opções marcadas e atualiza o texto na tela
                    selectedList.clear();
                    for (CheckBox cb : checkBoxes) {
                        if (cb.isChecked()) {
                            selectedList.add(cb.getText().toString());
                        }
                    }
                    updateTargetTextView(selectedList, targetTextView);
                })
                .setNeutralButton("Limpar", (dialog, which) -> {
                    // Botão para desmarcar tudo de uma vez
                    selectedList.clear();
                    updateTargetTextView(selectedList, targetTextView);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Atualiza o texto do botão de filtro conforme a quantidade de itens escolhidos
    private void updateTargetTextView(List<String> selectedList, TextView tv) {
        if (selectedList.isEmpty()) {
            tv.setText(R.string.todas);
        } else if (selectedList.size() == 1) {
            tv.setText(selectedList.get(0)); // Mostra o nome do único item
        } else {
            // Mostra algo como "2 selecionadas"
            String text = selectedList.size() + " " + getString(R.string.selecionadas_lower);
            tv.setText(text);
        }
    }

    // Configura a sincronia entre o Slider de preço e os campos de texto manual
    private void setupPriceFilter() {
        sliderPreco = findViewById(R.id.sliderPreco);
        editMinPreco = findViewById(R.id.editMinPreco);
        editMaxPreco = findViewById(R.id.editMaxPreco);

        // Quando o usuário arrasta o slider, atualiza o texto nos campos
        sliderPreco.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                isUpdating = true;
                editMinPreco.setText(formatCurrency(slider.getValues().get(0)));
                editMaxPreco.setText(formatCurrency(slider.getValues().get(1)));
                isUpdating = false;
            }
        });

        // Quando o campo perde o foco, aplica o valor digitado no slider
        View.OnFocusChangeListener focusListener = (v, hasFocus) -> {
            if (!hasFocus) applyPriceInputs();
        };

        // Quando o usuário aperta "Enter" no teclado, aplica os valores
        TextView.OnEditorActionListener editorActionListener = (v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                applyPriceInputs();
                return true;
            }
            return false;
        };

        editMinPreco.setOnFocusChangeListener(focusListener);
        editMaxPreco.setOnFocusChangeListener(focusListener);
        editMinPreco.setOnEditorActionListener(editorActionListener);
        editMaxPreco.setOnEditorActionListener(editorActionListener);

        // Define os valores iniciais
        editMinPreco.setText(formatCurrency(sliderPreco.getValues().get(0)));
        editMaxPreco.setText(formatCurrency(sliderPreco.getValues().get(1)));
    }

    // Converte o que foi digitado no campo de preço e ajusta a posição do Slider
    private void applyPriceInputs() {
        if (isUpdating) return;
        float min = parseCurrency(editMinPreco.getText().toString());
        float max = parseCurrency(editMaxPreco.getText().toString());
        
        // Garante que os valores estejam dentro do limite (0 a 10.000)
        min = Math.max(0, Math.min(10000, min));
        max = Math.max(0, Math.min(10000, max));
        if (max < min) max = min; // Impede que o máximo seja menor que o mínimo

        isUpdating = true;
        sliderPreco.setValues(min, max);
        editMinPreco.setText(formatCurrency(min));
        editMaxPreco.setText(formatCurrency(max));
        isUpdating = false;
    }

    // Formata o número como moeda brasileira (ex: 100.5 -> R$ 100,50)
    private String formatCurrency(float val) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return nf.format(val);
    }

    // Converte texto de dinheiro (com R$ e vírgula) de volta para número decimal
    private float parseCurrency(String input) {
        if (input == null || input.isEmpty()) return 0;
        String clean = input.replaceAll("[^0-9,]", "").replace(",", ".");
        try {
            return clean.isEmpty() ? 0 : Float.parseFloat(clean);
        } catch (Exception e) { return 0; }
    }

    // Configura a sincronia do filtro de Distância (Slider e Campos de Texto)
    private void setupDistanceFilter() {
        sliderDistancia = findViewById(R.id.sliderDistancia);
        editMinDistancia = findViewById(R.id.editMinDistancia);
        editMaxDistancia = findViewById(R.id.editMaxDistancia);

        // Atualiza campos de texto ao arrastar o slider de km
        sliderDistancia.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                isUpdating = true;
                editMinDistancia.setText(formatDistance(slider.getValues().get(0)));
                editMaxDistancia.setText(formatDistance(slider.getValues().get(1)));
                isUpdating = false;
            }
        });

        View.OnFocusChangeListener focusListener = (v, hasFocus) -> {
            if (!hasFocus) applyDistanceInputs();
        };

        TextView.OnEditorActionListener editorActionListener = (v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                applyDistanceInputs();
                return true;
            }
            return false;
        };

        editMinDistancia.setOnFocusChangeListener(focusListener);
        editMaxDistancia.setOnFocusChangeListener(focusListener);
        editMinDistancia.setOnEditorActionListener(editorActionListener);
        editMaxDistancia.setOnEditorActionListener(editorActionListener);

        // Define valores iniciais (0 km e 5 km)
        editMinDistancia.setText(formatDistance(sliderDistancia.getValues().get(0)));
        editMaxDistancia.setText(formatDistance(sliderDistancia.getValues().get(1)));
    }

    // Converte e valida os valores digitados no campo de distância
    private void applyDistanceInputs() {
        if (isUpdating) return;
        float min = parseDistanceSmart(editMinDistancia.getText().toString());
        float max = parseDistanceSmart(editMaxDistancia.getText().toString());

        // Limite de 0 a 5 km
        min = Math.max(0, Math.min(5, min));
        max = Math.max(0, Math.min(5, max));
        if (max < min) max = min;

        isUpdating = true;
        sliderDistancia.setValues(min, max);
        editMinDistancia.setText(formatDistance(min));
        editMaxDistancia.setText(formatDistance(max));
        isUpdating = false;
    }

    // Formata a distância: mostra metros se for < 1km, senão mostra km
    private String formatDistance(float valueInKm) {
        if (valueInKm < 1.0f) {
            int meters = (int) (valueInKm * 1000);
            return meters + " m";
        } else {
            DecimalFormat df = new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.getDefault()));
            return df.format(valueInKm) + " km";
        }
    }

    // Entende o que o usuário digita como distância (ex: "500m" vira 0.5km)
    private float parseDistanceSmart(String input) {
        if (input == null || input.isEmpty()) return 0;
        String lowInput = input.toLowerCase().trim();
        String clean = lowInput.replaceAll("[^0-9,.]", "").replace(",", ".");
        try {
            float val = clean.isEmpty() ? 0 : Float.parseFloat(clean);
            
            // Se o usuário escreveu "metro" ou só "m", divide por 1000
            if (lowInput.contains("metro") || (lowInput.contains("m") && !lowInput.contains("km"))) {
                return val / 1000f;
            }
            
            // Se digitou um número alto (ex: 500) sem unidade, assume que são metros
            if (!lowInput.contains("k") && val >= 10) {
                return val / 1000f;
            }
            
            return val; // Por padrão, assume que são quilômetros
        } catch (Exception e) { return 0; }
    }

    // Configura a barra de busca da tela inicial
    private void setupHomeSearch() {
        EditText editSearch = findViewById(R.id.editSearchHome);
        editSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                String query = editSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    Intent intent = new Intent(this, VitrineActivity.class);
                    intent.putExtra("searchQuery", query);
                    startActivity(intent);
                }
                return true;
            }
            return false;
        });
    }

}
