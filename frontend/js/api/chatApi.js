import { CONFIG } from "../config.js";

/**
 * fetch sin abortar prematuramente, con timeout opcional muy largo.
 */
async function fetchWithLongTimeout(url, options = {}, timeoutMs = CONFIG.TIMEOUT_MS) {
  // Solo aplicamos timeout si timeoutMs es > 0
  if (timeoutMs <= 0) {
    const res = await fetch(url, options);
    return res;
  }

  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), timeoutMs);

  try {
    const res = await fetch(url, { ...options, signal: controller.signal });
    return res;
  } finally {
    clearTimeout(timeoutId);
  }
}

/**
 * Función para preguntar al chatbot.
 * Devuelve un objeto { answer, tableData }.
 */
export async function askChatbot({ datasetId, question }) {
  // Opcional: puedes aumentar el timeout si sabes que Denodo tarda mucho
  const TIMEOUT_MS = 180000; // 3 minutos

  const res = await fetchWithLongTimeout(`${CONFIG.API_BASE_URL}/chat`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ datasetId, question })
  }, TIMEOUT_MS);

  let data = null;
  try { data = await res.json(); } catch (_) {}

  if (!res.ok) {
    const msg = data?.answer || data?.message || `Error HTTP ${res.status}`;
    throw new Error(msg);
  }

  if (!data?.answer) throw new Error("Respuesta inválida del servidor");

  return {
    answer: data.answer,
    tableData: data.tableData || null,
    metrics: data.metrics || null
  };
}