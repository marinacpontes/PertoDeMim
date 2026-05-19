package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// Tela do Perfil do Fornecedor selecionado
public class ProviderProfileActivity extends AppCompatActivity {

    private String providerName, category, address, rating, reviews;
    private final Set<Integer> selectedItems = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Define o layout da tela de perfil do fornecedor
        setContentView(R.layout.activity_provider_profile);

        loadData(); // Recupera dados enviados pela Vitrine
        initUI(); // Atualiza os textos da tela

        // Botão de voltar
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Botão para enviar mensagem
        findViewById(R.id.btnMessage).setOnClickListener(v -> {
            Intent intent = new Intent(this, ConversationActivity.class);
            intent.putExtra("contactName", providerName);
            startActivity(intent);
        });

        // Botão para contratar serviço
        findViewById(R.id.btnHire).setOnClickListener(v -> {
            if (selectedItems.isEmpty()) {
                android.widget.Toast.makeText(this, "Selecione pelo menos um serviço", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            openPayment();
        });

        // Botão para ver todas as avaliações
        findViewById(R.id.btnViewAllReviews).setOnClickListener(v -> {
            Intent intent = new Intent(this, AllReviewsActivity.class);
            intent.putExtra("providerName", providerName);
            startActivity(intent);
        });

        setupSocialActions();
        setupCatalogForProvider(); // Configura o catálogo específico para este fornecedor
        setupReviewsForProvider(); // Configura avaliações específicas
    }

    private void loadData() {
        Intent intent = getIntent();
        providerName = intent.getStringExtra("providerName");
        category = intent.getStringExtra("category");
        address = intent.getStringExtra("address");
        rating = intent.getStringExtra("rating");
        reviews = intent.getStringExtra("reviews");
    }

    private void initUI() {
        if (providerName != null) ((TextView) findViewById(R.id.tvProviderName)).setText(providerName);
        if (category != null) ((TextView) findViewById(R.id.tvProviderCategory)).setText(category);
        if (address != null) ((TextView) findViewById(R.id.tvProviderAddress)).setText(address);
        if (rating != null) ((TextView) findViewById(R.id.tvRatingValue)).setText(rating);
        
        if (reviews != null) {
            String reviewsText = reviews + " avaliações";
            ((TextView) findViewById(R.id.tvReviewsCount)).setText(reviewsText);
        }
        
        TextView tvDesc = findViewById(R.id.tvDescription);
        View banner = findViewById(R.id.providerBanner);
        TextView tvInsta = findViewById(R.id.tvProviderInstagram);
        TextView tvPhone = findViewById(R.id.tvProviderPhone);
        
        if ("Salão Bela Forma".equals(providerName)) {
            banner.setBackgroundResource(R.drawable.grad_pink);
            tvInsta.setText("@belaformal");
            tvPhone.setText("(11) 98765-4321");
            tvDesc.setText("Salão de beleza especializado em cortes modernos, coloração e tratamentos capilares.");
        } else if ("TechFix Consertos".equals(providerName)) {
            banner.setBackgroundResource(R.drawable.grad_blue);
            tvInsta.setText("@techfix_oficial");
            tvPhone.setText("(11) 91234-5678");
            tvDesc.setText("Especialistas em reparos de notebooks e smartphones. Peças originais e garantia.");
        } else if ("Pizzaria Napolitana".equals(providerName)) {
            banner.setBackgroundResource(R.drawable.grad_red);
            tvInsta.setText("@pizzaria_napoli");
            tvPhone.setText("(11) 3344-5566");
            tvDesc.setText("A verdadeira pizza napolitana com ingredientes importados e forno a lenha.");
        } else if ("Academia FitLife".equals(providerName)) {
            banner.setBackgroundResource(R.drawable.grad_green);
            tvInsta.setText("@fitlife_academia");
            tvPhone.setText("(11) 99887-7665");
            tvDesc.setText("Equipamentos modernos, instrutores qualificados e ambiente climatizado.");
        } else if ("Auto Mecânica Silva".equals(providerName)) {
            banner.setBackgroundResource(R.drawable.grad_gray);
            tvInsta.setText("@mecanica_silva");
            tvPhone.setText("(11) 4002-8922");
            tvDesc.setText("Oficina mecânica completa com diagnóstico de última geração.");
        } else if ("Escola de Idiomas Global".equals(providerName)) {
            banner.setBackgroundResource(R.drawable.grad_purple);
            tvInsta.setText("@idiomas_global");
            tvPhone.setText("(11) 3214-5678");
            tvDesc.setText("Aprenda um novo idioma de forma rápida com foco na conversação.");
        }
    }

    private void setupCatalogForProvider() {
        selectedItems.clear();
        int[] itemLayouts = {R.id.itemCatalog1, R.id.itemCatalog2, R.id.itemCatalog3, R.id.itemCatalog4, R.id.itemCatalog5};
        
        if ("Salão Bela Forma".equals(providerName)) {
            setCatalogItem(0, "Corte Masculino", "R$ 50,00", "30 min");
            setCatalogItem(1, "Corte Feminino", "R$ 70,00", "45 min");
            setCatalogItem(2, "Escova", "R$ 40,00", "40 min");
            setCatalogItem(3, "Manicure", "R$ 35,00", "30 min");
            setCatalogItem(4, "Corte + Escova", "R$ 80,00", "1h 15min");
        } else if ("TechFix Consertos".equals(providerName)) {
            setCatalogItem(0, "Manutenção de Notebook", "R$ 150,00", "1h 30min");
            setCatalogItem(1, "Limpeza Preventiva", "R$ 100,00", "1h");
            setCatalogItem(2, "Formatação + Backup", "R$ 150,00", "2h");
            setCatalogItem(3, "Upgrade SSD 240GB", "R$ 250,00", "1h");
            setCatalogItem(4, "Troca de Tela", "R$ 450,00", "3h");
        } else if ("Pizzaria Napolitana".equals(providerName)) {
            setCatalogItem(0, "Pizza Pequena", "R$ 30,00", "30 min");
            setCatalogItem(1, "Pizza Média", "R$ 40,00", "30 min");
            setCatalogItem(2, "Pizza Grande", "R$ 45,00", "35 min");
            setCatalogItem(3, "Refrigerante 2L", "R$ 12,00", "5 min");
            hideItem(4);
        } else if ("Academia FitLife".equals(providerName)) {
            setCatalogItem(0, "Plano Mensal", "R$ 120,00", "30 dias");
            setCatalogItem(1, "Plano Trimestral", "R$ 300,00", "90 dias");
            setCatalogItem(2, "Plano Anual", "R$ 1.000,00", "1 ano");
            setCatalogItem(3, "Avaliação Física", "R$ 50,00", "45 min");
            hideItem(4);
        } else if ("Auto Mecânica Silva".equals(providerName)) {
            setCatalogItem(0, "Troca de Óleo", "R$ 180,00", "40 min");
            setCatalogItem(1, "Alinhamento e Balanceamento", "R$ 120,00", "1h");
            setCatalogItem(2, "Revisão Completa", "R$ 280,00", "3h");
            setCatalogItem(3, "Limpeza de Bicos", "R$ 150,00", "1h 30min");
            hideItem(4);
        } else if ("Escola de Idiomas Global".equals(providerName)) {
            setCatalogItem(0, "Matrícula", "R$ 100,00", "-");
            setCatalogItem(1, "Mensalidade Inglês", "R$ 200,00", "Mensal");
            setCatalogItem(2, "Material Didático", "R$ 150,00", "-");
            setCatalogItem(3, "Aula Experimental", "R$ 30,00", "1h");
            hideItem(4);
        }

        for (int id : itemLayouts) {
            View v = findViewById(id);
            if (v == null) continue;
            v.setOnClickListener(view -> {
                if (selectedItems.contains(id)) {
                    selectedItems.remove(id);
                    view.setBackgroundResource(R.drawable.bg_payment_normal);
                } else {
                    selectedItems.add(id);
                    view.setBackgroundResource(R.drawable.bg_payment_selected);
                }
            });
        }
    }

    private void setCatalogItem(int index, String name, String price, String duration) {
        int[] nameIds = {R.id.tvCatalogName1, R.id.tvCatalogName2, R.id.tvCatalogName3, R.id.tvCatalogName4, R.id.tvCatalogName5};
        int[] priceIds = {R.id.tvCatalogPrice1, R.id.tvCatalogPrice2, R.id.tvCatalogPrice3, R.id.tvCatalogPrice4, R.id.tvCatalogPrice5};
        int[] durationIds = {R.id.tvCatalogDuration1, R.id.tvCatalogDuration2, R.id.tvCatalogDuration3, R.id.tvCatalogDuration4, R.id.tvCatalogDuration5};
        int[] layoutIds = {R.id.itemCatalog1, R.id.itemCatalog2, R.id.itemCatalog3, R.id.itemCatalog4, R.id.itemCatalog5};

        ((TextView) findViewById(nameIds[index])).setText(name);
        ((TextView) findViewById(priceIds[index])).setText(price);
        ((TextView) findViewById(durationIds[index])).setText(duration);
        findViewById(layoutIds[index]).setVisibility(View.VISIBLE);
    }

    private void hideItem(int index) {
        int[] layoutIds = {R.id.itemCatalog1, R.id.itemCatalog2, R.id.itemCatalog3, R.id.itemCatalog4, R.id.itemCatalog5};
        findViewById(layoutIds[index]).setVisibility(View.GONE);
    }

    private void setupReviewsForProvider() {
        if ("TechFix Consertos".equals(providerName)) {
            setReview(1, "RL", "Ricardo Lima", "10/04/2026", "Consertaram meu notebook super rápido. Preço justo.", 5);
            setReview(2, "FA", "Fernanda Alves", "05/04/2026", "Ótimo atendimento técnico. Recomendo o upgrade.", 5);
            setReview(3, "JP", "João Pedro", "01/04/2026", "Demorou um pouco mas ficou bom.", 3);
        } else if ("Pizzaria Napolitana".equals(providerName)) {
            setReview(1, "GP", "Giovanna Paula", "12/04/2026", "A melhor pizza da região! Massa fininha.", 5);
            setReview(2, "RB", "Roberto Brás", "10/04/2026", "Muito boa, mas veio um pouco fria hoje.", 4);
            setReview(3, "ML", "Mariana Luz", "05/04/2026", "Ingredientes de qualidade.", 5);
        } else if ("Academia FitLife".equals(providerName)) {
            setReview(1, "AM", "André Melo", "15/04/2026", "Equipamentos novos e instrutores atenciosos.", 5);
            setReview(2, "SC", "Sofia Castro", "12/04/2026", "Gosto das aulas, mas está sempre muito cheia.", 4);
            setReview(3, "LT", "Lucas Torres", "08/04/2026", "Melhor custo benefício.", 5);
        } else if ("Auto Mecânica Silva".equals(providerName)) {
            setReview(1, "PM", "Paulo Mendes", "14/04/2026", "Resolveram o problema do meu carro.", 5);
            setReview(2, "EF", "Eliane Farias", "11/04/2026", "Preço meio salgado, mas serviço é garantido.", 4);
            setReview(3, "JB", "Jorge Batista", "07/04/2026", "Não gostei da demora no atendimento.", 2);
        } else if ("Escola de Idiomas Global".equals(providerName)) {
            setReview(1, "BC", "Beatriz Costa", "13/04/2026", "As aulas são ótimas e bem dinâmicas.", 5);
            setReview(2, "RN", "Renato Nunes", "10/04/2026", "Professores excelentes.", 5);
            setReview(3, "TA", "Tiago Amorim", "06/04/2026", "Ótima localização.", 4);
        } else {
            setReview(1, "MS", "Maria Santos", "20/04/2026", "Excelente atendimento! Super recomendo!", 5);
            setReview(2, "CO", "Carlos Oliveira", "18/04/2026", "Bom serviço, ambiente agradável.", 4);
            setReview(3, "AP", "Ana Paula", "15/04/2026", "Adorei o resultado!", 5);
        }
    }

    private void setReview(int index, String avatar, String name, String date, String comment, int stars) {
        int avatarId = (index == 1) ? R.id.tvAvatar1 : (index == 2) ? R.id.tvAvatar2 : R.id.tvAvatar3;
        int nameId = (index == 1) ? R.id.tvReviewerName1 : (index == 2) ? R.id.tvReviewerName2 : R.id.tvReviewerName3;
        int dateId = (index == 1) ? R.id.tvReviewDate1 : (index == 2) ? R.id.tvReviewDate2 : R.id.tvReviewDate3;
        int commentId = (index == 1) ? R.id.tvReviewComment1 : (index == 2) ? R.id.tvReviewComment2 : R.id.tvReviewComment3;
        int starsContainerId = (index == 1) ? R.id.llStars1 : (index == 2) ? R.id.llStars2 : R.id.llStars3;

        ((TextView) findViewById(avatarId)).setText(avatar);
        ((TextView) findViewById(nameId)).setText(name);
        ((TextView) findViewById(dateId)).setText(date);
        ((TextView) findViewById(commentId)).setText(comment);

        LinearLayout starsContainer = findViewById(starsContainerId);
        if (starsContainer != null) {
            for (int i = 0; i < 5; i++) {
                ImageView star = (ImageView) starsContainer.getChildAt(i);
                if (i < stars) {
                    star.setImageResource(R.drawable.ic_star);
                    star.setColorFilter(androidx.core.content.ContextCompat.getColor(this, R.color.terracota));
                } else {
                    star.setImageResource(R.drawable.ic_star_outline);
                    star.setColorFilter(null);
                }
            }
        }
    }

    private void openPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> prices = new ArrayList<>();
        ArrayList<String> durations = new ArrayList<>();
        
        int[] layouts = {R.id.itemCatalog1, R.id.itemCatalog2, R.id.itemCatalog3, R.id.itemCatalog4, R.id.itemCatalog5};
        int[] nameIds = {R.id.tvCatalogName1, R.id.tvCatalogName2, R.id.tvCatalogName3, R.id.tvCatalogName4, R.id.tvCatalogName5};
        int[] priceIds = {R.id.tvCatalogPrice1, R.id.tvCatalogPrice2, R.id.tvCatalogPrice3, R.id.tvCatalogPrice4, R.id.tvCatalogPrice5};
        int[] durationIds = {R.id.tvCatalogDuration1, R.id.tvCatalogDuration2, R.id.tvCatalogDuration3, R.id.tvCatalogDuration4, R.id.tvCatalogDuration5};

        for (int i = 0; i < layouts.length; i++) {
            if (selectedItems.contains(layouts[i])) {
                names.add(((TextView) findViewById(nameIds[i])).getText().toString());
                prices.add(((TextView) findViewById(priceIds[i])).getText().toString());
                durations.add(((TextView) findViewById(durationIds[i])).getText().toString());
            }
        }

        intent.putStringArrayListExtra("serviceNames", names);
        intent.putStringArrayListExtra("servicePrices", prices);
        intent.putStringArrayListExtra("serviceDurations", durations);
        intent.putExtra("providerName", providerName);
        intent.putExtra("category", category);
        intent.putExtra("address", address);
        startActivity(intent);
    }

    private void setupSocialActions() {
        findViewById(R.id.llPhone).setOnClickListener(v -> 
            android.widget.Toast.makeText(this, "Ligando para o prestador...", android.widget.Toast.LENGTH_SHORT).show());
        findViewById(R.id.llInstagram).setOnClickListener(v -> 
            android.widget.Toast.makeText(this, "Abrindo Instagram...", android.widget.Toast.LENGTH_SHORT).show());
    }
}