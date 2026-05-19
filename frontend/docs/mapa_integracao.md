# Integracao com Google Maps — Perto de Mim

Documentacao para implementacao do mapa no app Android.

---

## Como funciona

O backend ja fornece os dados necessarios. O mapa e implementado **100% no front**.

```
GET /fornecedores
        ↓
Recebe lista de fornecedores com enderecos
        ↓
Google Maps SDK converte endereco em coordenadas (Geocoding)
        ↓
Plota pins no mapa para cada fornecedor
        ↓
Usuario clica no pin → abre perfil do fornecedor
```

---

## Dados disponiveis na API

A rota `GET http://10.0.2.2:3000/fornecedores` retorna:

```json
[
  {
    "id": 1,
    "nome": "Marina Silva",
    "tipo": "fornecedor",
    "logradouro": "Av. Beira Mar",
    "cidade": "Fortaleza",
    "estado": "CE",
    "fornecedor_id": 1,
    "nome_loja": "Studio Marina",
    "nome_responsavel": "Marina Silva",
    "categoria": "Beleza e Estetica",
    "descricao": "Especialista em cabelo e maquiagem",
    "preco_medio": "80"
  }
]
```

O endereco completo para o Geocoding e:
```
logradouro + ", " + cidade + " - " + estado
```

Exemplo: `"Av. Beira Mar, Fortaleza - CE"`

---

## Configuracao do Google Maps no Android

### 1. Adicionar dependencias no `build.gradle.kts`

```kotlin
dependencies {
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
}
```

### 2. Obter a API Key

1. Acessa [https://console.cloud.google.com](https://console.cloud.google.com)
2. Cria um projeto ou usa o existente
3. Ativa as APIs:
   - **Maps SDK for Android**
   - **Geocoding API**
4. Cria uma credencial → **API Key**

### 3. Adicionar a API Key no `AndroidManifest.xml`

```xml
<application>
    <meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="SUA_API_KEY_AQUI" />
</application>
```

### 4. Adicionar permissao de localizacao no `AndroidManifest.xml`

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

---

## Implementacao da tela do mapa

### Layout `activity_mapa.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<fragment
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/map"
    android:name="com.google.android.gms.maps.SupportMapFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

### Activity `MapaActivity.java`

```java
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.maps.android.PolyUtil;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        SupportMapFragment mapFragment = (SupportMapFragment)
            getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Centraliza o mapa em Fortaleza inicialmente
        LatLng fortaleza = new LatLng(-3.7172, -38.5433);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fortaleza, 12));

        // Busca os fornecedores da API
        buscarFornecedores();
    }

    private void buscarFornecedores() {
        // Usa o Retrofit que ja esta configurado no projeto
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.listarFornecedores().enqueue(new Callback<List<Fornecedor>>() {
            @Override
            public void onResponse(Call<List<Fornecedor>> call, Response<List<Fornecedor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Fornecedor fornecedor : response.body()) {
                        adicionarPinNoMapa(fornecedor);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Fornecedor>> call, Throwable t) {
                Toast.makeText(MapaActivity.this, "Erro ao carregar fornecedores", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void adicionarPinNoMapa(Fornecedor fornecedor) {
        // Monta o endereco completo
        String endereco = fornecedor.getLogradouro() + ", " +
                          fornecedor.getCidade() + " - " +
                          fornecedor.getEstado();

        // Converte endereco em coordenadas usando Geocoder
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(endereco, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Adiciona o pin no mapa
                mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(fornecedor.getNomeLoja())
                    .snippet(fornecedor.getCategoria() + " • R$ " + fornecedor.getPrecoMedio()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

---

## Adicionar rota na API do Retrofit

No arquivo `ApiService.java` adiciona:

```java
@GET("fornecedores")
Call<List<Fornecedor>> listarFornecedores();
```

---

## Modelo `Fornecedor.java`

```java
public class Fornecedor {
    @SerializedName("id")
    private int id;

    @SerializedName("nome_loja")
    private String nomeLoja;

    @SerializedName("categoria")
    private String categoria;

    @SerializedName("preco_medio")
    private String precoMedio;

    @SerializedName("logradouro")
    private String logradouro;

    @SerializedName("cidade")
    private String cidade;

    @SerializedName("estado")
    private String estado;

    @SerializedName("descricao")
    private String descricao;

    // Getters
    public int getId() { return id; }
    public String getNomeLoja() { return nomeLoja; }
    public String getCategoria() { return categoria; }
    public String getPrecoMedio() { return precoMedio; }
    public String getLogradouro() { return logradouro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getDescricao() { return descricao; }
}
```

---

## Clicar no pin e abrir perfil do fornecedor

```java
mMap.setOnMarkerClickListener(marker -> {
    // Busca o fornecedor pelo titulo do marker
    // e abre a tela de perfil
    Intent intent = new Intent(MapaActivity.this, PerfilFornecedorActivity.class);
    intent.putExtra("fornecedor_id", fornecedorId);
    startActivity(intent);
    return true;
});
```

---

## Observacoes importantes

1. **API Key gratuita:** o Google Maps oferece $200 de credito mensal gratuito — mais que suficiente para testes de faculdade.
2. **Geocoding tem limite:** para muitos fornecedores considere salvar latitude/longitude no banco para nao chamar o Geocoder toda vez.
3. **Permissao de localizacao:** pedir a permissao em tempo de execucao no Android 6+.
4. **URL base:** usar `http://10.0.2.2:3000` no emulador e `http://IP_DA_MAQUINA:3000` no celular fisico.