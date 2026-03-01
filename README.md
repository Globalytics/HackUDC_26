## HACKUDC 2026
# Decision Tool using Denodo AI SDK

Proyecto desarrollado para el HackUDC basado en el reto propuesto por Denodo Technologies.  
El objetivo es construir una herramienta de toma de decisiones basada en datos utilizando el Denodo AI SDK.

La aplicación analiza datasets disponibles en el Data Marketplace y genera recomendaciones fundamentadas a partir de los datos.

---

## Objetivo del proyecto

El sistema permite:

- Explorar automáticamente datasets disponibles.
- Analizar metadatos para entender qué información existe.
- Consultar datos relevantes.
- Generar recomendaciones basadas en análisis de datos.

A diferencia de un chatbot tradicional, la herramienta está orientada a **analítica y toma de decisiones**.

---

## Arquitectura del sistema

El proyecto sigue una arquitectura basada en análisis de datos y consultas inteligentes.

### Data Integration

- Importación de datasets en Denodo.
- Creación de data sources.
- Definición de relaciones (joins).

### Metadata Discovery

Uso del endpoint:
