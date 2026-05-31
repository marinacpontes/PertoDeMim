package com.app.pertodemim;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.RangeSlider;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VitrineActivity extends AppCompatActivity {

    private TextView chipTodos, chipBeleza, chipTecnologia, chipAlimentacao, chipSaude, chipManutencao, chipEducacao;
    private MaterialCardView card1, card2, card3, card4, card5, card6;
    private final List<TextView> allChips = new ArrayList<>();
    private RangeSlider sliderPreco, sliderDistancia;
    private EditText editMinPreco, editMaxPreco, editMinDistancia, editMaxDistancia;
    private TextView tvSelectedAvaliacao, tvSelectedCategoria;
    private View panelPreco, panelDistancia, filtersContainer;
    private boolean isUpdating = false;
    private final List<String> selectedAvaliacoes = new ArrayList<>();
    private final List<String> selectedCategorias = new ArrayList<>();
    private String currentSearchQuery = "";
    private String currentChipCategory = "todos";

    private static class ServiceData {
        MaterialCardView card;
        String name, provider, category;
        double price, distance, rating;
        ServiceData(MaterialCardView card, String name, String provider, String category, double price, double distance, double rating) {
            this.card = card; this.name = name; this.provider = provider; this.category = category;
            this.price = price; this.distance = distance; this.rating = rating;
        }
    }
    private final List<ServiceData> serviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitrine);
        initUI();
        setupBottomNavigation();
        setupFilters();
        setupDrawerFilters();

        serviceList.add(new ServiceData(card1, "Corte + Escova", "Salão Bela Forma", getString(R.string.beleza_estetica), 80.0, 0.5, 4.8));
        serviceList.add(new ServiceData(card2, "Manutenção de Notebook", "TechFix Consertos", getString(R.string.tecnologia), 150.0, 1.2, 4.5));
        serviceList.add(new ServiceData(card3, "Pizza Grande", "Pizzaria Napolitana", getString(R.string.alimentacao), 45.0, 0.8, 4.9));
        serviceList.add(new ServiceData(card4, "Plano Mensal", "Academia FitLife", getString(R.string.saude), 120.0, 1.5, 4.6));
        serviceList.add(new ServiceData(card5, "Revisão Completa", "Auto Mecânica Silva", getString(R.string.manutencao), 280.0, 2.0, 4.7));
        serviceList.add(new ServiceData(card6, "Curso de Inglês", "Escola de Idiomas Global", getString(R.string.educacao), 200.0, 1.0, 4.9));

        card1.setOnClickListener(v -> openProfile("Corte + Escova", "Salão Bela Forma", getString(R.string.beleza_estetica), "R$ 80,00", "1h 15min", "Rua das Flores, 123 - Centro", "4.8", "120"));
        card2.setOnClickListener(v -> openProfile("Manutenção de Notebook", "TechFix Consertos", getString(R.string.tecnologia), "R$ 150,00", "2h 30min", "Av. Paulista, 900 - Bela Vista", "4.5", "89"));
        card3.setOnClickListener(v -> openProfile("Pizza Grande", "Pizzaria Napolitana", getString(R.string.alimentacao), "R$ 45,00", "45min", "Rua Augusta, 1500 - Consolação", "4.9", "250"));
        card4.setOnClickListener(v -> openProfile("Plano Mensal", "Academia FitLife", getString(R.string.saude), "R$ 120,00", "Mensal", "Rua Haddock Lobo, 300 - Jardins", "4.6", "180"));
        card5.setOnClickListener(v -> openProfile("Revisão Completa", "Auto Mecânica Silva", getString(R.string.manutencao), "R$ 280,00", "4h", "Av. Brigadeiro, 200 - Centro", "4.7", "95"));
        card6.setOnClickListener(v -> openProfile("Curso de Inglês", "Escola de Idiomas Global", getString(R.string.educacao), "R$ 200,00/mês", "1h/aula", "Rua Oscar Freire, 500 - Pinheiros", "4.9", "310"));

        setupSearch();
        String initialQuery = getIntent().getStringExtra("searchQuery");
        if (initialQuery != null) {
            EditText editSearch = findViewById(R.id.editSearchVitrine);
            if (editSearch != null) {
                editSearch.setText(initialQuery);
                currentSearchQuery = initialQuery.toLowerCase();
                applyFilters();
            }
        }
    }

    private void setupDrawerFilters() {
        filtersContainer = findViewById(R.id.filtersContainerVitrine);
        findViewById(R.id.btnMenuVitrine).setOnClickListener(v -> filtersContainer.setVisibility(View.VISIBLE));
        findViewById(R.id.btnCloseFiltersVitrine).setOnClickListener(v -> { filtersContainer.setVisibility(View.GONE); hideAllPanels(); });

        panelPreco = findViewById(R.id.panelPreco);
        panelDistancia = findViewById(R.id.panelDistancia);
        findViewById(R.id.btnFiltroPreco).setOnClickListener(v -> togglePanel(panelPreco));
        findViewById(R.id.btnFiltroDistancia).setOnClickListener(v -> togglePanel(panelDistancia));

        tvSelectedAvaliacao = findViewById(R.id.tvSelectedAvaliacao);
        tvSelectedCategoria = findViewById(R.id.tvSelectedCategoria);

        findViewById(R.id.btnFiltroAvaliacao).setOnClickListener(v -> showMultiSelectDialog("Avaliação", new String[]{"1 estrela", "2 estrelas", "3 estrelas", "4 estrelas", "5 estrelas"}, selectedAvaliacoes, tvSelectedAvaliacao));
        findViewById(R.id.btnFiltroCategoria).setOnClickListener(v -> showMultiSelectDialog(getString(R.string.categoria), new String[]{getString(R.string.beleza_estetica), getString(R.string.saude), getString(R.string.alimentacao), getString(R.string.educacao), getString(R.string.manutencao), getString(R.string.tecnologia), "Outros"}, selectedCategorias, tvSelectedCategoria));

        setupPriceFilter(); setupDistanceFilter();
        findViewById(R.id.btnApplyFilters).setOnClickListener(v -> { applyFilters(); filtersContainer.setVisibility(View.GONE); });
    }

    private void togglePanel(View panel) { if (panel.getVisibility() == View.VISIBLE) panel.setVisibility(View.GONE); else { hideAllPanels(); panel.setVisibility(View.VISIBLE); } }
    private void hideAllPanels() { panelPreco.setVisibility(View.GONE); panelDistancia.setVisibility(View.GONE); }

    private void showMultiSelectDialog(String title, String[] options, List<String> selectedList, TextView targetTextView) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_filtros_multi, null);
        LinearLayout container = view.findViewById(R.id.llOptionsContainer);
        List<CheckBox> checkBoxes = new ArrayList<>();
        for (String option : options) {
            CheckBox cb = new CheckBox(this); cb.setText(option); cb.setPadding(16, 16, 16, 16); cb.setChecked(selectedList.contains(option));
            container.addView(cb); checkBoxes.add(cb);
        }
        new AlertDialog.Builder(this).setTitle(title).setView(view).setPositiveButton("OK", (dialog, which) -> {
                    selectedList.clear(); for (CheckBox cb : checkBoxes) if (cb.isChecked()) selectedList.add(cb.getText().toString());
                    updateTargetTextView(selectedList, targetTextView);
                }).setNeutralButton("Limpar", (dialog, which) -> { selectedList.clear(); updateTargetTextView(selectedList, targetTextView); }).setNegativeButton("Cancelar", null).show();
    }

    private void updateTargetTextView(List<String> selectedList, TextView tv) {
        if (selectedList.isEmpty()) tv.setText(R.string.todas);
        else if (selectedList.size() == 1) tv.setText(selectedList.get(0));
        else tv.setText(selectedList.size() + " " + getString(R.string.selecionadas_lower));
    }

    private void setupPriceFilter() {
        sliderPreco = findViewById(R.id.sliderPreco); editMinPreco = findViewById(R.id.editMinPreco); editMaxPreco = findViewById(R.id.editMaxPreco);
        sliderPreco.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) { isUpdating = true; editMinPreco.setText(formatCurrency(slider.getValues().get(0))); editMaxPreco.setText(formatCurrency(slider.getValues().get(1))); isUpdating = false; }
        });
        View.OnFocusChangeListener focusListener = (v, hasFocus) -> { if (!hasFocus) applyPriceInputs(); };
        TextView.OnEditorActionListener editorActionListener = (v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) { applyPriceInputs(); return true; }
            return false;
        };
        editMinPreco.setOnFocusChangeListener(focusListener); editMaxPreco.setOnFocusChangeListener(focusListener);
        editMinPreco.setOnEditorActionListener(editorActionListener); editMaxPreco.setOnEditorActionListener(editorActionListener);
        editMinPreco.setText(formatCurrency(sliderPreco.getValues().get(0))); editMaxPreco.setText(formatCurrency(sliderPreco.getValues().get(1)));
    }

    private void applyPriceInputs() {
        if (isUpdating) return;
        float inputMin = parseCurrency(editMinPreco.getText().toString()); float inputMax = parseCurrency(editMaxPreco.getText().toString());
        float finalMin = Math.max(0, Math.min(10000, inputMin)); float finalMax = Math.max(0, Math.min(10000, inputMax));
        if (finalMax < finalMin) finalMax = finalMin;
        isUpdating = true; sliderPreco.setValues(finalMin, finalMax); editMinPreco.setText(formatCurrency(finalMin)); editMaxPreco.setText(formatCurrency(finalMax)); isUpdating = false;
    }

    private String formatCurrency(float val) { return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(val); }
    private float parseCurrency(String input) {
        if (input == null || input.isEmpty()) return 0;
        String clean = input.replaceAll("[^0-9,]", "").replace(",", ".");
        try { return clean.isEmpty() ? 0 : Float.parseFloat(clean); } catch (Exception e) { return 0; }
    }

    private void setupDistanceFilter() {
        sliderDistancia = findViewById(R.id.sliderDistancia); editMinDistancia = findViewById(R.id.editMinDistancia); editMaxDistancia = findViewById(R.id.editMaxDistancia);
        sliderDistancia.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) { isUpdating = true; editMinDistancia.setText(formatDistance(slider.getValues().get(0))); editMaxDistancia.setText(formatDistance(slider.getValues().get(1))); isUpdating = false; }
        });
        View.OnFocusChangeListener focusListener = (v, hasFocus) -> { if (!hasFocus) applyDistanceInputs(); };
        TextView.OnEditorActionListener editorActionListener = (v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) { applyDistanceInputs(); return true; }
            return false;
        };
        editMinDistancia.setOnFocusChangeListener(focusListener); editMaxDistancia.setOnFocusChangeListener(focusListener);
        editMinDistancia.setOnEditorActionListener(editorActionListener); editMaxDistancia.setOnEditorActionListener(editorActionListener);
        editMinDistancia.setText(formatDistance(sliderDistancia.getValues().get(0))); editMaxDistancia.setText(formatDistance(sliderDistancia.getValues().get(1)));
    }

    private void applyDistanceInputs() {
        if (isUpdating) return;
        float min = parseDistanceSmart(editMinDistancia.getText().toString()); float max = parseDistanceSmart(editMaxDistancia.getText().toString());
        min = Math.max(0, Math.min(5, min)); max = Math.max(0, Math.min(5, max));
        if (max < min) max = min;
        isUpdating = true; sliderDistancia.setValues(min, max); editMinDistancia.setText(formatDistance(min)); editMaxDistancia.setText(formatDistance(max)); isUpdating = false;
    }

    private String formatDistance(float valueInKm) {
        if (valueInKm < 1.0f) return (int) (valueInKm * 1000) + " m";
        else { DecimalFormat df = new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.getDefault())); return df.format(valueInKm) + " km"; }
    }

    private float parseDistanceSmart(String input) {
        if (input == null || input.isEmpty()) return 0;
        String lowInput = input.toLowerCase().trim(); String clean = lowInput.replaceAll("[^0-9,.]", "").replace(",", ".");
        try {
            float val = clean.isEmpty() ? 0 : Float.parseFloat(clean);
            if (lowInput.contains("metro") || (lowInput.contains("m") && !lowInput.contains("km"))) return val / 1000f;
            if (!lowInput.contains("k") && val >= 10) return val / 1000f;
            return val;
        } catch (Exception e) { return 0; }
    }

    private void applyFilters() {
        float minPrice = sliderPreco.getValues().get(0); float maxPrice = sliderPreco.getValues().get(1);
        float minDist = sliderDistancia.getValues().get(0); float maxDist = sliderDistancia.getValues().get(1);

        for (ServiceData item : serviceList) {
            boolean matchesPrice = item.price >= minPrice && item.price <= maxPrice;
            boolean matchesDist = item.distance >= minDist && item.distance <= maxDist;
            boolean matchesRating = selectedAvaliacoes.isEmpty();
            if (!selectedAvaliacoes.isEmpty()) {
                for (String sel : selectedAvaliacoes) {
                    int stars = Integer.parseInt(sel.split(" ")[0]);
                    if (item.rating >= stars && item.rating < stars + 1) { matchesRating = true; break; }
                }
            }
            boolean matchesCategory = selectedCategorias.isEmpty() || selectedCategorias.contains(item.category);
            boolean matchesChip = currentChipCategory.equals("todos") || item.category.equals(getStringCategory(currentChipCategory));
            boolean matchesSearch = currentSearchQuery.isEmpty() || item.name.toLowerCase().contains(currentSearchQuery) || item.provider.toLowerCase().contains(currentSearchQuery);
            item.card.setVisibility(matchesPrice && matchesDist && matchesRating && matchesCategory && matchesChip && matchesSearch ? View.VISIBLE : View.GONE);
        }
    }

    private String getStringCategory(String chipId) {
        switch (chipId) {
            case "beleza": return getString(R.string.beleza_estetica); case "tecnologia": return getString(R.string.tecnologia);
            case "alimentacao": return getString(R.string.alimentacao); case "saude": return getString(R.string.saude);
            case "manutencao": return getString(R.string.manutencao); case "educacao": return getString(R.string.educacao);
            default: return "";
        }
    }

    private void setupSearch() {
        EditText editSearch = findViewById(R.id.editSearchVitrine);
        if (editSearch == null) return;
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { currentSearchQuery = s.toString().toLowerCase(); applyFilters(); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void openProfile(String service, String provider, String category, String price, String duration, String address, String rating, String reviews) {
        Intent intent = new Intent(this, ProviderProfileActivity.class);
        intent.putExtra("serviceName", service); intent.putExtra("providerName", provider); intent.putExtra("category", category);
        intent.putExtra("price", price); intent.putExtra("duration", duration); intent.putExtra("address", address);
        intent.putExtra("rating", rating); intent.putExtra("reviews", reviews); startActivity(intent);
    }

    private void initUI() {
        chipTodos = findViewById(R.id.chipTodos); chipBeleza = findViewById(R.id.chipBeleza); chipTecnologia = findViewById(R.id.chipTecnologia);
        chipAlimentacao = findViewById(R.id.chipAlimentacao); chipSaude = findViewById(R.id.chipSaude); chipManutencao = findViewById(R.id.chipManutencao); chipEducacao = findViewById(R.id.chipEducacao);
        allChips.add(chipTodos); allChips.add(chipBeleza); allChips.add(chipTecnologia); allChips.add(chipAlimentacao); allChips.add(chipSaude); allChips.add(chipManutencao); allChips.add(chipEducacao);
        card1 = findViewById(R.id.cardService1); card2 = findViewById(R.id.cardService2); card3 = findViewById(R.id.cardService3); card4 = findViewById(R.id.cardService4); card5 = findViewById(R.id.cardService5); card6 = findViewById(R.id.cardService6);
    }

    private void setupFilters() {
        chipTodos.setOnClickListener(v -> filterCategory("todos", chipTodos)); chipBeleza.setOnClickListener(v -> filterCategory("beleza", chipBeleza));
        chipTecnologia.setOnClickListener(v -> filterCategory("tecnologia", chipTecnologia)); chipAlimentacao.setOnClickListener(v -> filterCategory("alimentacao", chipAlimentacao));
        chipSaude.setOnClickListener(v -> filterCategory("saude", chipSaude)); chipManutencao.setOnClickListener(v -> filterCategory("manutencao", chipManutencao));
        chipEducacao.setOnClickListener(v -> filterCategory("educacao", chipEducacao));
    }

    private void filterCategory(String category, TextView selectedChip) {
        currentChipCategory = category;
        for (TextView chip : allChips) {
            if (chip == selectedChip) { chip.setBackgroundResource(R.drawable.bg_category_chip_selected); chip.setTextColor(ContextCompat.getColor(this, R.color.white)); chip.setTypeface(null, Typeface.BOLD); }
            else { chip.setBackgroundResource(R.drawable.bg_category_chip_normal); chip.setTextColor(ContextCompat.getColor(this, R.color.verde_petroleo_profundo)); chip.setTypeface(null, Typeface.NORMAL); }
        }
        applyFilters();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation); bottomNavigation.setSelectedItemId(R.id.nav_vitrine);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_mapa) { startActivity(new Intent(this, HomeActivity.class)); finish(); return true; }
            else if (id == R.id.nav_chat) { startActivity(new Intent(this, ChatActivity.class)); finish(); return true; }
            else if (id == R.id.nav_perfil) { startActivity(new Intent(this, ProfileActivity.class)); finish(); return true; }
            return true;
        });
    }
}
