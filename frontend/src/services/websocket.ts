import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

export function connectWebsocket(onMessage: (msg: string) => void) {
  const url = import.meta.env.VITE_API_WS ?? 'http://localhost:8080/ws-albuns';

  const client: any = new Client({
    brokerURL: undefined,
    webSocketFactory: () => new SockJS(url) as any,
    reconnectDelay: 5000,
    onConnect: () => {
      client.subscribe('/topic/albuns', (message: any) => {
        if (message && message.body) onMessage(message.body);
      });
    },
  });

  client.activate();

  return () => client.deactivate();
}
