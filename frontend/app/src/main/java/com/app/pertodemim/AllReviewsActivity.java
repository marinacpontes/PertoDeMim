package com.app.pertodemim;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;

// Tela que exibe todas as avaliações de um fornecedor com filtros reais
public class AllReviewsActivity extends AppCompatActivity {

    private LinearLayout llReviewsContainer;
    private String providerName;
    private final List<Review> allReviews = new ArrayList<>();

    // Classe simples para representar uma avaliação
    private static class Review {
        String avatar, name, date, comment;
        int stars;
        boolean hasPhoto;

        Review(String avatar, String name, String date, String comment, int stars, boolean hasPhoto) {
            this.avatar = avatar; this.name = name; this.date = date; 
            this.comment = comment; this.stars = stars; this.hasPhoto = hasPhoto;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reviews);

        llReviewsContainer = findViewById(R.id.llReviewsContainer);
        providerName = getIntent().getStringExtra("providerName");

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        generateMockData(); // Cria a lista de dados inicial
        applyFilters(); // Aplica filtros e exibe
        setupFilters(); // Configura os cliques nos chips
    }

    private void setupFilters() {
        // Chip "Todas" reseta os outros
        findViewById(R.id.chipAll).setOnClickListener(v -> {
            resetChips();
            applyFilters();
        });

        // Configura listener de mudança em cada chip de filtro
        int[] chipIds = {R.id.chipWithPhoto, R.id.chip5Stars, R.id.chip4Stars, R.id.chip3Stars, R.id.chip2Stars, R.id.chip1Star};
        for (int id : chipIds) {
            ((Chip) findViewById(id)).setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) ((Chip) findViewById(R.id.chipAll)).setChecked(false);
                applyFilters();
            });
        }
    }

    private void resetChips() {
        int[] chipIds = {R.id.chipWithPhoto, R.id.chip5Stars, R.id.chip4Stars, R.id.chip3Stars, R.id.chip2Stars, R.id.chip1Star};
        for (int id : chipIds) ((Chip) findViewById(id)).setChecked(false);
        ((Chip) findViewById(R.id.chipAll)).setChecked(true);
    }

    private void applyFilters() {
        llReviewsContainer.removeAllViews();
        
        boolean filterPhoto = ((Chip) findViewById(R.id.chipWithPhoto)).isChecked();
        boolean filter5 = ((Chip) findViewById(R.id.chip5Stars)).isChecked();
        boolean filter4 = ((Chip) findViewById(R.id.chip4Stars)).isChecked();
        boolean filter3 = ((Chip) findViewById(R.id.chip3Stars)).isChecked();
        boolean filter2 = ((Chip) findViewById(R.id.chip2Stars)).isChecked();
        boolean filter1 = ((Chip) findViewById(R.id.chip1Star)).isChecked();
        boolean anyStarFilter = filter5 || filter4 || filter3 || filter2 || filter1;

        for (Review r : allReviews) {
            boolean matchesPhoto = !filterPhoto || r.hasPhoto;
            // Se houver filtros de estrela selecionados, a review deve bater com ALGUM deles (OR entre estrelas)
            boolean matchesStars = !anyStarFilter || 
                    (filter5 && r.stars == 5) || 
                    (filter4 && r.stars == 4) || 
                    (filter3 && r.stars == 3) || 
                    (filter2 && r.stars == 2) || 
                    (filter1 && r.stars == 1);

            if (matchesPhoto && matchesStars) {
                addReviewToUI(r);
            }
        }

        findViewById(R.id.tvEmpty).setVisibility(llReviewsContainer.getChildCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void addReviewToUI(Review r) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_review, llReviewsContainer, false);
        ((TextView) view.findViewById(R.id.tvReviewAvatar)).setText(r.avatar);
        ((TextView) view.findViewById(R.id.tvReviewName)).setText(r.name);
        ((TextView) view.findViewById(R.id.tvReviewDate)).setText(r.date);
        ((TextView) view.findViewById(R.id.tvReviewComment)).setText(r.comment);

        LinearLayout llStars = view.findViewById(R.id.llStars);
        for (int i = 0; i < 5; i++) {
            ImageView star = (ImageView) llStars.getChildAt(i);
            if (i < r.stars) {
                star.setImageResource(R.drawable.ic_star);
                star.setColorFilter(ContextCompat.getColor(this, R.color.terracota));
            } else {
                star.setImageResource(R.drawable.ic_star_outline);
                star.setColorFilter(null);
            }
        }
        llReviewsContainer.addView(view);
    }

    private void generateMockData() {
        allReviews.clear();
        
        if ("TechFix Consertos".equals(providerName)) {
            allReviews.add(new Review("RL", "Ricardo Lima", "10/04/2026", "Consertaram meu notebook super rápido. Aqui está a foto do serviço.", 5, true));
            allReviews.add(new Review("FA", "Fernanda Alves", "05/04/2026", "Ótimo atendimento técnico. Recomendo o upgrade.", 5, false));
            allReviews.add(new Review("JP", "João Pedro", "01/04/2026", "Demorou um pouco, mas o resultado final foi aceitável.", 3, false));
            allReviews.add(new Review("MT", "Marcos Tulio", "28/03/2026", "Preço justo pelo serviço prestado. Aqui está a foto do serviço.", 4, true));
        } else if ("Pizzaria Napolitana".equals(providerName)) {
            allReviews.add(new Review("GP", "Giovanna Paula", "12/04/2026", "A melhor pizza da região! Aqui está a foto do serviço.", 5, true));
            allReviews.add(new Review("ML", "Mariana Luz", "05/04/2026", "Ingredientes de qualidade, sabor incomparável.", 5, false));
            allReviews.add(new Review("RB", "Roberto Brás", "10/04/2026", "Muito boa, mas veio um pouco fria hoje. Aqui está a foto do serviço.", 4, true));
            allReviews.add(new Review("LC", "Lucas Costa", "02/04/2026", "Demorou um pouco mais que o esperado.", 3, false));
        } else if ("Academia FitLife".equals(providerName)) {
            allReviews.add(new Review("AM", "André Melo", "15/04/2026", "Equipamentos novos e instrutores atenciosos.", 5, false));
            allReviews.add(new Review("SC", "Sofia Castro", "12/04/2026", "Gosto das aulas, mas está sempre muito cheia.", 4, false));
            allReviews.add(new Review("LT", "Lucas Torres", "08/04/2026", "Melhor custo benefício da cidade.", 5, false));
        } else {
            // Salão Bela Forma ou outros
            allReviews.add(new Review("MS", "Maria Santos", "20/04/2026", "Excelente atendimento! Aqui está a foto do serviço.", 5, true));
            allReviews.add(new Review("AP", "Ana Paula", "15/04/2026", "Adorei o resultado!", 5, false));
            allReviews.add(new Review("CO", "Carlos Oliveira", "18/04/2026", "Bom serviço, ambiente agradável.", 4, false));
            allReviews.add(new Review("RL", "Ricardo Lima", "10/04/2026", "Muito satisfeito. Aqui está a foto do serviço.", 4, true));
            allReviews.add(new Review("JP", "João Pedro", "01/04/2026", "Demorou um pouco.", 3, false));
        }
    }
}