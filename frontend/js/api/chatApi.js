import { CONFIG } from "../config.js";

async function fetchWithTimeout(url, options = {}) {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), CONFIG.TIMEOUT_MS);

  try {
    return await fetch(url, { ...options, signal: controller.signal });
  } finally {
    clearTimeout(timeoutId);
  }
}

export async function askChatbot({ datasetId, question }) {
  const res = await fetchWithTimeout(`${CONFIG.API_BASE_URL}/chat`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ datasetId, question })
  });

  let data = null;
  try { data = await res.json(); } catch (_) {}

  if (!res.ok) {
    const msg = data?.answer || data?.message || `Error HTTP ${res.status}`;
    throw new Error(msg);
  }

  if (!data?.answer) throw new Error("Respuesta inválida del servidor");
  return data.answer;
}