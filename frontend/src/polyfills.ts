// Pequeno polyfill para bibliotecas que esperam `global` no ambiente browser
// Garante compatibilidade com `sockjs-client` e outras libs antigas.
(window as any).global = window;
