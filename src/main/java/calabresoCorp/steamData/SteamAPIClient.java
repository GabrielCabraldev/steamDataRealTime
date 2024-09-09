package calabresoCorp.steamData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SteamAPIClient {
    private static final String API_KEY = "17E3199D634564CAA69759598A60F9CB";
    private static final String STEAM_APP_LIST_URL = "https://api.steampowered.com/ISteamApps/GetAppList/v2/";
    private static final String STEAM_APP_DETAILS_URL = "https://store.steampowered.com/api/appdetails?appids=";
    private static final String STEAM_PLAYER_COUNT_URL = "https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o nome do jogo: ");
        String nomeJogoProcurado = scanner.nextLine();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest requestAppList = HttpRequest.newBuilder()
                    .uri(URI.create(STEAM_APP_LIST_URL))
                    .GET()
                    .build();

            HttpResponse<String> responseAppList = client.send(requestAppList, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNodeAppList = objectMapper.readTree(responseAppList.body());
            JsonNode jogos = jsonNodeAppList.get("applist").get("apps");

            int appId = -1;

            for (JsonNode jogo : jogos) {
                if (jogo.get("name").asText().equalsIgnoreCase(nomeJogoProcurado)) {
                    System.out.println("AppID: " + jogo.get("appid").asInt());
                    appId = jogo.get("appid").asInt();
                    break;
                }
            }

            if (appId != -1) {
                HttpRequest requestAppDetails = HttpRequest.newBuilder()
                        .uri(URI.create(STEAM_APP_DETAILS_URL + appId))
                        .GET()
                        .build();

                HttpResponse<String> responseAppDetails = client.send(requestAppDetails, HttpResponse.BodyHandlers.ofString());
                JsonNode jsonNodeAppDetails = objectMapper.readTree(responseAppDetails.body());
                JsonNode gameDetails = jsonNodeAppDetails.get(String.valueOf(appId)).get("data");

                if (gameDetails != null) {
                    if (gameDetails.has("name") && !gameDetails.get("name").isNull()) {
                        System.out.println("Nome do Jogo: " + gameDetails.get("name").asText());
                    } else {
                        System.out.println("Nome do Jogo: Não disponível");
                    }

                    if (gameDetails.has("short_description") && !gameDetails.get("short_description").isNull()) {
                        System.out.println("Descrição: " + gameDetails.get("short_description").asText());
                    } else {
                        System.out.println("Descrição: Não disponível");
                    }

                    JsonNode developersNode = gameDetails.get("developers");
                    if (developersNode != null && developersNode.isArray() && developersNode.size() > 0) {
                        System.out.println("Desenvolvedor: " + developersNode.get(0).asText());
                    } else {
                        System.out.println("Desenvolvedor: Não disponível");
                    }

                    JsonNode publishersNode = gameDetails.get("publishers");
                    if (publishersNode != null && publishersNode.isArray() && publishersNode.size() > 0) {
                        System.out.println("Publicador: " + publishersNode.get(0).asText());
                    } else {
                        System.out.println("Publicador: Não disponível");
                    }

                    JsonNode priceOverviewNode = gameDetails.get("price_overview");
                    if (priceOverviewNode != null && priceOverviewNode.has("final_formatted") && !priceOverviewNode.get("final_formatted").isNull()) {
                        System.out.println("Preço: " + priceOverviewNode.get("final_formatted").asText());
                    } else {
                        System.out.println("Preço: Gratuito");
                    }

                    JsonNode pcRequirementsNode = gameDetails.get("pc_requirements");
                    if (pcRequirementsNode != null && pcRequirementsNode.has("minimum") && !pcRequirementsNode.get("minimum").isNull()) {
                        System.out.println("Requisitos de Sistema (Windows): " + pcRequirementsNode.get("minimum").asText());
                    } else {
                        System.out.println("Requisitos de Sistema (Windows): Não disponível");
                    }

                    try {
                        HttpRequest requestPlayerCount = HttpRequest.newBuilder()
                                .uri(URI.create(STEAM_PLAYER_COUNT_URL + appId))
                                .GET()
                                .build();

                        HttpResponse<String> responsePlayerCount = client.send(requestPlayerCount, HttpResponse.BodyHandlers.ofString());
                        JsonNode jsonNodePlayerCount = objectMapper.readTree(responsePlayerCount.body());
                        JsonNode responseNode = jsonNodePlayerCount.get("response");
                        if (responseNode != null && responseNode.has("player_count") && !responseNode.get("player_count").isNull()) {
                            int playerCount = responseNode.get("player_count").asInt();
                            System.out.println("Jogadores Online Agora: " + playerCount);
                        } else {
                            System.out.println("Jogadores Online Agora: Não disponível");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro ao obter a contagem de jogadores online: " + e.getMessage());
                    }

                } else {
                    System.out.println("Detalhes do jogo não encontrados.");
                }

            } else {
                System.out.println("Jogo não encontrado.");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {

            scanner.close();
        }
    }
}
