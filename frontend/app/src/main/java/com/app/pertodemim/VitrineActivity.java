package com.app.pertodemim;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

// Tela de Vitrine de Serviços com filtro por categoria
public class VitrineActivity extends AppCompatActivity {

    // Componentes de interface (filtros de categoria e cards de serviço)
    private TextView chipTodos, chipBeleza, chipTecnologia, chipAlimentacao, chipSaude, chipManutencao, chipEducacao;
    private MaterialCardView card1, card2, card3, card4, card5, card6;
    private final List<TextView> allChips = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Define o layout da tela de vitrine
        setContentView(R.layout.activity_vitrine);

        initUI(); // Inicializa os componentes da tela
        setupBottomNavigation(); // Configura a navegação inferior
        setupFilters(); // Configura a lógica dos botões de filtro

        // Configura o clique em todos os cards para abrir o perfil passando os dados do serviço
        card1.setOnClickListener(v -> openProfile("Corte + Escova", "Salão Bela Forma", getString(R.string.beleza_estetica), "R$ 80,00", "1h 15min", "Rua das Flores, 123 - Centro", "4.8", "120"));
        card2.setOnClickListener(v -> openProfile("Manutenção de Notebook", "TechFix Consertos", getString(R.string.tecnologia), "R$ 150,00", "2h 30min", "Av. Paulista, 900 - Bela Vista", "4.5", "89"));
        card3.setOnClickListener(v -> openProfile("Pizza Grande", "Pizzaria Napolitana", getString(R.string.alimentacao), "R$ 45,00", "45min", "Rua Augusta, 1500 - Consolação", "4.9", "250"));
        card4.setOnClickListener(v -> openProfile("Plano Mensal", "Academia FitLife", getString(R.string.saude), "R$ 120,00", "Mensal", "Rua Haddock Lobo, 300 - Jardins", "4.6", "180"));
        card5.setOnClickListener(v -> openProfile("Revisão Completa", "Auto Mecânica Silva", getString(R.string.manutencao), "R$ 280,00", "4h", "Av. Brigadeiro, 200 - Centro", "4.7", "95"));
        card6.setOnClickListener(v -> openProfile("Curso de Inglês", "Escola de Idiomas Global", getString(R.string.educacao), "R$ 200,00/mês", "1h/aula", "Rua Oscar Freire, 500 - Pinheiros", "4.9", "310"));

        setupSearch(); // Configura a barra de pesquisa

        // Verifica se veio uma busca da tela inicial
        String initialQuery = getIntent().getStringExtra("searchQuery");
        if (initialQuery != null) {
            EditText editSearch = findViewById(R.id.editSearchVitrine);
            if (editSearch != null) {
                editSearch.setText(initialQuery);
                searchServices(initialQuery.toLowerCase());
            }
        }
    }

    // Configura a lógica da barra de busca por texto
    private void setupSearch() {
        EditText editSearch = findViewById(R.id.editSearchVitrine);
        if (editSearch == null) return;

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchServices(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Filtra os cards baseado no texto digitado
    private void searchServices(String query) {
        if (query.isEmpty()) {
            filterCategory("todos", chipTodos); // Reseta para o filtro atual se busca vazia
            return;
        }

        // Simulação de busca: verifica se o nome do serviço contém a query
        card1.setVisibility("salao bela forma corte escova".contains(query) ? View.VISIBLE : View.GONE);
        card2.setVisibility("notebook conserto tecnico".contains(query) ? View.VISIBLE : View.GONE);
        card3.setVisibility("pizzaria napolitana comida jantar".contains(query) ? View.VISIBLE : View.GONE);
        card4.setVisibility("academia fitlife fitness treino".contains(query) ? View.VISIBLE : View.GONE);
        card5.setVisibility("manutencao residencial reparos".contains(query) ? View.VISIBLE : View.GONE);
        card6.setVisibility("curso ingles educacao aulas".contains(query) ? View.VISIBLE : View.GONE);
    }

    // Abre a tela de perfil do fornecedor com os dados do serviço selecionado
    private void openProfile(String service, String provider, String category, String price, String duration, String address, String rating, String reviews) {
        Intent intent = new Intent(this, ProviderProfileActivity.class);
        intent.putExtra("serviceName", service);
        intent.putExtra("providerName", provider);
        intent.putExtra("category", category);
        intent.putExtra("price", price);
        intent.putExtra("duration", duration);
        intent.putExtra("address", address);
        intent.putExtra("rating", rating);
        intent.putExtra("reviews", reviews);
        startActivity(intent);
    }

    // Liga as variáveis aos IDs do layout XML
    private void initUI() {
        // Inicializa os botões (chips) de categoria
        chipTodos = findViewById(R.id.chipTodos);
        chipBeleza = findViewById(R.id.chipBeleza);
        chipTecnologia = findViewById(R.id.chipTecnologia);
        chipAlimentacao = findViewById(R.id.chipAlimentacao);
        chipSaude = findViewById(R.id.chipSaude);
        chipManutencao = findViewById(R.id.chipManutencao);
        chipEducacao = findViewById(R.id.chipEducacao);

        // Adiciona todos na lista para facilitar a manipulação visual posterior
        allChips.add(chipTodos); allChips.add(chipBeleza); allChips.add(chipTecnologia);
        allChips.add(chipAlimentacao); allChips.add(chipSaude); allChips.add(chipManutencao);
        allChips.add(chipEducacao);

        // Inicializa os cards de serviço da lista
        card1 = findViewById(R.id.cardService1);
        card2 = findViewById(R.id.cardService2);
        card3 = findViewById(R.id.cardService3);
        card4 = findViewById(R.id.cardService4);
        card5 = findViewById(R.id.cardService5);
        card6 = findViewById(R.id.cardService6);
    }

    // Configura os cliques para cada botão de categoria
    private void setupFilters() {
        chipTodos.setOnClickListener(v -> filterCategory("todos", chipTodos));
        chipBeleza.setOnClickListener(v -> filterCategory("beleza", chipBeleza));
        chipTecnologia.setOnClickListener(v -> filterCategory("tecnologia", chipTecnologia));
        chipAlimentacao.setOnClickListener(v -> filterCategory("alimentacao", chipAlimentacao));
        chipSaude.setOnClickListener(v -> filterCategory("saude", chipSaude));
        chipManutencao.setOnClickListener(v -> filterCategory("manutencao", chipManutencao));
        chipEducacao.setOnClickListener(v -> filterCategory("educacao", chipEducacao));
    }

    // Lógica que muda o visual dos botões e mostra/esconde os serviços conforme a categoria
    private void filterCategory(String category, TextView selectedChip) {
        // Atualiza as cores e o estilo (negrito) dos botões
        for (TextView chip : allChips) {
            if (chip == selectedChip) {
                // Estilo para o botão selecionado
                chip.setBackgroundResource(R.drawable.bg_category_chip_selected);
                chip.setTextColor(ContextCompat.getColor(this, R.color.white));
                chip.setTypeface(null, Typeface.BOLD);
            } else {
                // Estilo para os botões não selecionados
                chip.setBackgroundResource(R.drawable.bg_category_chip_normal);
                chip.setTextColor(ContextCompat.getColor(this, R.color.verde_petroleo_profundo));
                chip.setTypeface(null, Typeface.NORMAL);
            }
        }

        // Mostra ou esconde os cards baseados na categoria selecionada
        card1.setVisibility(category.equals("todos") || category.equals("beleza") ? View.VISIBLE : View.GONE);
        card2.setVisibility(category.equals("todos") || category.equals("tecnologia") ? View.VISIBLE : View.GONE);
        card3.setVisibility(category.equals("todos") || category.equals("alimentacao") ? View.VISIBLE : View.GONE);
        card4.setVisibility(category.equals("todos") || category.equals("saude") ? View.VISIBLE : View.GONE);
        card5.setVisibility(category.equals("todos") || category.equals("manutencao") ? View.VISIBLE : View.GONE);
        card6.setVisibility(category.equals("todos") || category.equals("educacao") ? View.VISIBLE : View.GONE);
    }

    // Configura a barra de navegação inferior (Menu)
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_vitrine); // Marca o item Vitrine como ativo
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_mapa) {
                // Vai para o mapa
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_chat) {
                // Vai para o chat
                startActivity(new Intent(this, ChatActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_perfil) {
                // Vai para o perfil
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return true;
        });
    }
}