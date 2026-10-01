# DocRefiner AI

A Spring Boot backend that uses **two AI agents working together** to turn rough developer notes into polished documentation.

## The Problem

Developers usually write documentation once and stop. The result is often unclear, incomplete, or missing examples. A single LLM call has the same weakness — it produces whatever it produces on the first try and doesn't improve itself.

## The Solution

Two AI agents in a feedback loop:

1. **Writer Agent** — takes a topic and rough notes, produces a documentation draft.
2. **Evaluator Agent** — reads the draft and critiques it, pointing out what's missing or unclear.

The Writer then revises based on the Evaluator's feedback. This repeats until the Evaluator approves or the maximum number of rounds is reached. The result is better documentation than any single AI pass could produce.

## How It Works
User sends topic + rough notes
↓
┌─────────────┐
│ Writer │ ← produces or revises draft
└─────────────┘
↓
┌─────────────┐
│ Evaluator │ ← critiques the draft
└─────────────┘
↓
Approved? ── No ──→ back to Writer (with feedback)
│
Yes
↓
Return final draft + feedback history

## Why Local AI (No API Costs)

Uses **Ollama** to run models locally. No OpenAI keys, no cloud bills, no internet required after setup.

## Tech Stack

- Java 21
- Spring Boot 3.x
- Spring AI
- Ollama (local LLM runtime)

## Setup

1. Install [Ollama](https://ollama.com) and pull a model:
   ```bash
   ollama pull mistral
Start Ollama:

bash
ollama serve
Run the Spring Boot app:

bash
./mvnw spring-boot:run
Usage
Send a POST request: Using postman or any tool and in the request body you provide a json topic and some rough content.
