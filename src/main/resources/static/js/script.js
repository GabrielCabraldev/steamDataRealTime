function buscarJogo() {
    const nomeJogo = document.getElementById('nomeJogo').value;

    fetch(`http://localhost:8080/buscar-jogo?nome=${encodeURIComponent(nomeJogo)}`)
        .then(response => response.json())
        .then(data => {
            const resultadosDiv = document.getElementById('resultados');
            if (!data || !data.name) {
                resultadosDiv.textContent = "Jogo não encontrado.";
            } else {
                let detalhes = `
                    <h2>${data.name}</h2>
                    <p><strong>Descrição:</strong> ${data.description}</p>
                    <p><strong>Desenvolvedor:</strong> ${data.developers.join(", ")}</p>
                    <p><strong>Publicador:</strong> ${data.publishers.join(", ")}</p>
                    <p><strong>Preço:</strong> ${data.price}</p>
                `;
                resultadosDiv.innerHTML = detalhes;

                // Buscar jogadores online
                buscarJogadoresOnline(data.appId);
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            document.getElementById('resultados').textContent = "Erro ao buscar informações do jogo.";
        });
}

function buscarJogadoresOnline(appId) {
    fetch(`http://localhost:8080/jogadores-online?appId=${appId}`)
        .then(response => response.json())
        .then(data => {
            const resultadosDiv = document.getElementById('resultados');
            let jogadoresOnline = `<p><strong>Jogadores Online Agora:</strong> ${data.playerCount}</p>`;
            resultadosDiv.innerHTML += jogadoresOnline;
        })
        .catch(error => {
            console.error('Erro:', error);
        });
}
