const cells = document.querySelectorAll(".cell");
const messageElement = document.getElementById("message");
const newGameButton = document.getElementById("new-game-button");

let gameActive = false;
let requestInProgress = false;

let gameState = createEmptyGameState();

function createEmptyGameState() {
    return {
        board: "{0,0,0,0,0,0,0,0,0}",
        hashBoard: "",
        nonce: 0,
        hashNonce: "",
        timestamp: 0,
        hashTimestamp: "",
        isFinished: false,
    };
}

function encodeClientRequest(request) {
    const move = request.move ?? 0;
    const board = request.board ?? "";
    const hashBoard = request.hashBoard ?? "";
    const nonce = request.nonce ?? 0;
    const hashNonce = request.hashNonce ?? "";
    const timestamp = request.timestamp ?? 0;
    const hashTimestamp = request.hashTimestamp ?? "";

    return [move, board, hashBoard, nonce, hashNonce, timestamp, hashTimestamp]
        .join(";")
        .replace(/^/, "[")
        .replace(/$/, "]");
}

function decodeServerResponse(raw) {
    console.log(raw);
    if (typeof raw !== "string") {
        throw new Error("Server response must be a string.");
    }

    raw = raw.trim();

    if (!raw.startsWith("[") || !raw.endsWith("]")) {
        throw new Error("Invalid server response format.");
    }

    const contents = raw.substring(1, raw.length - 1);
    const parts = contents.split(";");

    if (parts.length !== 8) {
        throw new Error(
            `Invalid server response: expected 8 fields, received ${parts.length}.`,
        );
    }

    if (parts[0] !== "0" && parts[0] !== "1") {
        throw new Error("Invalid finished value.");
    }

    const nonce = Number.parseInt(parts[4], 10);
    const timestamp = Number.parseInt(parts[6], 10);

    if (Number.isNaN(nonce)) {
        throw new Error("Invalid nonce.");
    }

    if (Number.isNaN(timestamp)) {
        throw new Error("Invalid timestamp.");
    }

    return {
        isFinished: parts[0] === "1",
        board: parts[1],
        message: parts[2],
        hashBoard: parts[3],
        nonce: nonce,
        hashNonce: parts[5],
        timestamp: timestamp,
        hashTimestamp: parts[7],
    };
}

async function sendRequest(clientRequest) {
    const requestRaw = encodeClientRequest(clientRequest);

    const httpResponse = await fetch("game", {
        method: "POST",
        headers: {
            "Content-Type": "text/plain; charset=UTF-8",
        },
        body: requestRaw,
    });

    const responseRaw = await httpResponse.text();

    if (!httpResponse.ok) {
        throw new Error(
            `Server returned HTTP ${httpResponse.status}: ${responseRaw}`,
        );
    }

    return decodeServerResponse(responseRaw);
}

async function startGame() {
    if (gameActive || requestInProgress) {
        return;
    }

    requestInProgress = true;
    gameState = createEmptyGameState();

    messageElement.textContent = "Starting game...";
    updateInterface();

    try {
        const response = await sendRequest(createEmptyGameState());

        updateGameState(response);

        gameActive = !response.isFinished;
        messageElement.textContent = response.message;
    } catch (error) {
        console.error(error.message);

        gameActive = false;
        messageElement.textContent = "Could not start the game.";
    } finally {
        requestInProgress = false;
        updateInterface();
    }
}

async function makeMove(move) {
    if (!gameActive || requestInProgress) {
        return;
    }

    const boardIndex = move - 1;

    if (
        gameState.board.length === 9 &&
        gameState.board[boardIndex * 2 + 1] !== "0"
    ) {
        return;
    }

    requestInProgress = true;
    messageElement.textContent = "Waiting for server...";
    updateInterface();

    try {
        const response = await sendRequest({
            move: move,
            board: gameState.board,
            hashBoard: gameState.hashBoard,
            nonce: gameState.nonce,
            hashNonce: gameState.hashNonce,
            timestamp: gameState.timestamp,
            hashTimestamp: gameState.hashTimestamp,
        });

        updateGameState(response);

        gameActive = !response.isFinished;
        messageElement.textContent = response.message;
    } catch (error) {
        console.error(error);
        messageElement.textContent = "Could not send the move.";
    } finally {
        requestInProgress = false;
        updateInterface();
    }
}

function updateGameState(response) {
    gameState = {
        board: response.board,
        hashBoard: response.hashBoard,
        nonce: response.nonce,
        hashNonce: response.hashNonce,
        timestamp: response.timestamp,
        hashTimestamp: response.hashTimestamp,
        isFinished: response.isFinished,
    };
}

function updateInterface() {
    renderBoard();

    /*
     * Disabled while a game is active or an HTTP request is running.
     * It becomes enabled again after the game finishes.
     */
    newGameButton.disabled = gameActive || requestInProgress;
}

function renderBoard() {
    cells.forEach(function (cell, index) {
        const value = gameState.board[index * 2 + 1];

        if (value === "1") {
            cell.textContent = "X";
        } else if (value === "2") {
            cell.textContent = "O";
        } else {
            cell.textContent = "";
        }

        const occupied = value === "1" || value === "2";

        cell.disabled =
            !gameActive ||
            requestInProgress ||
            gameState.isFinished ||
            occupied;
    });
}

newGameButton.addEventListener("click", startGame);

cells.forEach(function (cell) {
    cell.addEventListener("click", function () {
        const move = Number.parseInt(cell.dataset.move);
        makeMove(move);
    });
});

updateInterface();
