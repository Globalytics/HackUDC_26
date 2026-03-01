// frontend/js/chatbot.js
// Entry point for chatbot.html (served by Live Server on http://localhost:5500)
//
// Backend contract assumed:
//  - GET  http://localhost:8080/api/datasets  -> [{ id, name }]
//  - POST http://localhost:8080/api/chat      -> { answer }
//
// If your backend uses different routes/fields, change CONFIG + parsing below.

const CONFIG = {
  API_BASE_URL: "http://localhost:8080/api",
  TIMEOUT_MS: 300000
};

async function fetchWithTimeout(url, options = {}) {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), CONFIG.TIMEOUT_MS);

  try {
    return await fetch(url, { ...options, signal: controller.signal });
  } finally {
    clearTimeout(timeoutId);
  }
}

function appendMessage(container, role, text) {
  const div = document.createElement("div");
  // Reusa tus clases existentes: "message bot" / "message user"
  div.className = `message ${role === "user" ? "user" : "bot"}`;
  div.textContent = text;
  container.appendChild(div);
  container.scrollTop = container.scrollHeight;
}

function setChatEnabled(enabled) {
  const userInput = document.querySelector("#userInput");
  const sendBtn = document.querySelector("#sendBtn");
  userInput.disabled = !enabled;
  sendBtn.disabled = !enabled;
}

function setDatasetSelectEnabled(enabled) {
  const datasetSelect = document.querySelector("#datasetSelect");
  datasetSelect.disabled = !enabled;
}

function resetChatMessages(container) {
  container.innerHTML = "";
  appendMessage(container, "bot", "Please select a dataset to start chatting.");
}

async function loadDatasetsIntoSelect() {
   const datasetSelect = document.querySelector("#datasetSelect");
  const datasetHint = document.querySelector("#datasetHint");

  datasetHint.textContent = "";

  // Datasets fijos
  const datasets = [
    { id: "f1_races", name: "f1_races" },
    { id: "jjoo2024", name: "jjoo2024" }
  ];

  datasetSelect.innerHTML = `<option value="" selected disabled>Choose a dataset</option>`;

  for (const ds of datasets) {
    const opt = document.createElement("option");
    opt.value = ds.id;
    opt.textContent = ds.name;
    datasetSelect.appendChild(opt);
  }

  datasetHint.textContent = "Select one dataset to enable the chat.";
}

async function askChatbot(datasetId, question) {
  const res = await fetchWithTimeout(`${CONFIG.API_BASE_URL}/chat`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ datasetId, question })
  });

  let data = null;
  try { data = await res.json(); } catch (_) {}

  if (!res.ok) {
    const msg = data?.answer || data?.message || `Chat failed (HTTP ${res.status})`;
    throw new Error(msg);
  }

  const answer = data?.answer ?? data?.response ?? data?.message;
  if (!answer) throw new Error("Invalid response from server (missing 'answer').");
  return answer;
}

document.addEventListener("DOMContentLoaded", async () => {
  const datasetSelect = document.querySelector("#datasetSelect");
  const chatMessages = document.querySelector("#chatMessages");
  const chatForm = document.querySelector("#chatForm");
  const userInput = document.querySelector("#userInput");

  // Initial state
  setChatEnabled(false);
  resetChatMessages(chatMessages);

  // Load datasets
  try {
    await loadDatasetsIntoSelect();
  } catch (e) {
    datasetSelect.innerHTML = `<option value="" selected disabled>Error loading datasets</option>`;
    const datasetHint = document.querySelector("#datasetHint");
    datasetHint.textContent = `Error: ${e.message}`;
  }

  // Enable chat when dataset selected
  datasetSelect.addEventListener("change", () => {
    const datasetId = datasetSelect.value;
    resetChatMessages(chatMessages);

    if (!datasetId) {
      setChatEnabled(false);
      appendMessage(chatMessages, "bot", "Please select a dataset to start chatting.");
      return;
    }

    setChatEnabled(true);
    appendMessage(chatMessages, "bot", `Dataset selected: ${datasetSelect.options[datasetSelect.selectedIndex].text}`);
    userInput.focus();
  });

  // Handle send
  chatForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const datasetId = datasetSelect.value;
    const question = userInput.value.trim();

    if (!datasetId) {
      appendMessage(chatMessages, "bot", "Select a dataset first.");
      return;
    }
    if (!question) return;

    appendMessage(chatMessages, "user", question);
    userInput.value = "";

    // Lock UI while requesting
    setChatEnabled(false);
    setDatasetSelectEnabled(false);

    try {
      const answer = await askChatbot(datasetId, question);
      appendMessage(chatMessages, "bot", answer);
    } catch (err) {
      appendMessage(chatMessages, "bot", `Error: ${err.message}`);
    } finally {
      setChatEnabled(true);
      setDatasetSelectEnabled(true);
      userInput.focus();
    }
  });
});