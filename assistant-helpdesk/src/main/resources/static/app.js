const { useState, useEffect, useRef, useCallback } = React;

const apiService = {
    stompClient: null,
    sessionId: null,
    messageCallback: null,
    connectionStatusCallback: null,
    
    connect(onMessage, onStatusChange) {
        this.messageCallback = onMessage;
        this.connectionStatusCallback = onStatusChange;
        
        this.connectionStatusCallback('connecting');
        const socket = new SockJS('/chat-websocket');
        this.stompClient = Stomp.over(socket);
        
        // Disabilita i log di Stomp per evitare di intasare la console
        this.stompClient.debug = null;
        
        this.stompClient.connect({}, frame => {
            if (!frame.headers['sessionId']) {
                this.sessionId = new Date().getTime().toString();
            } else {
                this.sessionId = /\/[^\/]+\/([^\/]+)\//.exec(frame.headers['sessionId'])[1];
            }
            
            // Sottoscrizione al topic personale
            this.stompClient.subscribe('/topic/chat.' + this.sessionId, response => {
                const responseData = JSON.parse(response.body);
                this.messageCallback(responseData.responseText);
            });
            
            this.connectionStatusCallback('connected');
            
            // Caricare la cronologia qui se necessario
            return this.sessionId;
            
        }, error => {
            console.error('WebSocket connection error:', error);
            this.connectionStatusCallback('error');
        });
        
        return this.sessionId;
    },
    
    disconnect() {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.disconnect();
        }
        this.stompClient = null;
        this.sessionId = null;
        this.connectionStatusCallback('disconnected');
    },
    
    sendChatMessage(message) {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.send("/app/chat.message", {}, 
                JSON.stringify({
                    "sessionId": this.sessionId, 
                    "message": message
                })
            );
            return true;
        }
        return false;
    },

    getSession() {
        return this.sessionId;
    },
    
    isConnected() {
        return this.stompClient && this.stompClient.connected;
    },
    
    async getHistory(memoryId) {
        return fetch(`/message/history?memoryId=${memoryId || this.sessionId}`).then(response => {
            if (!response.ok) throw new Error('Failed to fetch chat history');
            return response.json();
        });
    },
};

const NotificationGT = ({ message, type, onClose }) => {
    useEffect(() => {
        const timer = setTimeout(onClose, 3000);
        lucide.createIcons();
        return () => clearTimeout(timer);
    }, [onClose]);

    const bgColor = type === 'error' ? 'bg-red-500' : 'bg-green-500';
    const icon = type === 'error' ? 'alert-circle' : 'check-circle';

    return (
        <div className={`${bgColor} text-white text-sm px-4 py-2 rounded-lg shadow-lg fade-in flex items-center`}>
            <i data-lucide={icon} className="w-4 h-4 mr-2"></i>
            {message}
        </div>
    );
};

const ConnectionStatus = ({ status }) => {
    const statusConfig = {
        disconnected: { text: 'Disconnesso', color: 'text-red-500', icon: 'wifi-off' },
        connecting: { text: 'Connessione in corso...', color: 'text-yellow-500', icon: 'loader' },
        connected: { text: 'Connesso', color: 'text-green-500', icon: 'wifi' },
        error: { text: 'Errore di connessione', color: 'text-red-500', icon: 'alert-circle' }
    };
    
    const config = statusConfig[status] || statusConfig.disconnected;
    
    useEffect(() => {
        lucide.createIcons();
    }, [status]);
    
    return (
        <div className={`flex items-center ${config.color} text-xs font-medium`}>
            <i data-lucide={config.icon} className="w-3 h-3 mr-1"></i>
            <span>{config.text}</span>
        </div>
    );
};

const ChatGT = () => {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [notification, setNotification] = useState(null);
    const [connectionStatus, setConnectionStatus] = useState('disconnected');
    const [sessionId, setSessionId] = useState(() => localStorage.getItem('memoryId') || generateID());

    const messagesEndRef = useRef(null);
    const inputRef = useRef(null);

    useEffect(() => {
        localStorage.setItem('memoryId', sessionId);
    }, [sessionId]);

    const scrollToBottom = useCallback(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, []);

    const handleReceiveMessage = useCallback((responseText) => {
        setMessages(prev => [...prev, { text: responseText, isUser: false }]);
        setIsLoading(false);
        setTimeout(scrollToBottom, 100);
    }, [scrollToBottom]);

    const handleConnectionStatus = useCallback((status) => {
        setConnectionStatus(status);
        
        if (status === 'connected') {
            setNotification({ message: 'Connessione stabilita!', type: 'success' });
        } else if (status === 'error') {
            setNotification({ message: 'Errore di connessione', type: 'error' });
        }
    }, []);

    useEffect(() => {
        // Connettere WebSocket all'avvio
        apiService.connect(handleReceiveMessage, handleConnectionStatus);
        
        // Caricare la cronologia dei messaggi
        const fetchHistory = async () => {
            try {
                const history = await apiService.getHistory(sessionId);
                if (!history.messages) return;
                const formattedHistory = history.messages.map(([text, isUser]) => ({ text, isUser: Boolean(isUser) }));
                setMessages(formattedHistory);
                scrollToBottom();
            } catch (error) {
                console.error('Error fetching chat history:', error);
            } finally {
                lucide.createIcons();
            }
        };

        fetchHistory();
        
        // Disconnettere quando il componente viene smontato
        return () => {
            apiService.disconnect();
        };
    }, [sessionId, scrollToBottom, handleReceiveMessage, handleConnectionStatus]);

    useEffect(() => {
        inputRef.current?.focus();
    }, []);

    const sendMessage = useCallback(
        async (e) => {
            e.preventDefault();
            const trimmedInput = input.trim();
            if (!trimmedInput) return;
            
            // Verificare se la connessione WebSocket è attiva
            if (!apiService.isConnected()) {
                setNotification({ message: 'Nessuna connessione attiva', type: 'error' });
                return;
            }

            setIsLoading(true);
            setMessages((prev) => [...prev, { text: trimmedInput, isUser: true }]);
            setInput('');

            try {
                const sent = apiService.sendChatMessage(trimmedInput);
                if (!sent) {
                    throw new Error('Impossibile inviare il messaggio');
                }
                // La risposta arriverà tramite il callback WebSocket
            } catch (error) {
                setMessages((prev) => [...prev, { text: error.message, isUser: false }]);
                setIsLoading(false);
            } finally {
                inputRef.current?.focus();
                scrollToBottom();
            }
        },
        [input, scrollToBottom]
    );

    const resetChat = useCallback(() => {
        // Disconnettere e riconnettere con nuovo ID
        apiService.disconnect();
        const newSessionId = generateID();
        setSessionId(newSessionId);
        setMessages([]);
        setInput('');
        apiService.connect(handleReceiveMessage, handleConnectionStatus);
        setNotification({ message: 'Chat history has been reset.', type: 'success' });
    }, [handleReceiveMessage, handleConnectionStatus]);

    const toggleConnection = useCallback(() => {
        if (apiService.isConnected()) {
            apiService.disconnect();
        } else {
            apiService.connect(handleReceiveMessage, handleConnectionStatus);
        }
    }, [handleReceiveMessage, handleConnectionStatus]);

    return (
        <div className="md:w-[600px] w-full mx-auto bg-white rounded-xl overflow-hidden">
            <HeaderGT connectionStatus={connectionStatus} />
            <ChatWindowGT
                messages={messages}
                isLoading={isLoading}
                sendMessage={sendMessage}
                input={input}
                setInput={setInput}
                inputRef={inputRef}
                messagesEndRef={messagesEndRef}
                connectionStatus={connectionStatus}
            />
            <ActionsGT
                resetChat={resetChat}
                toggleConnection={toggleConnection}
                connectionStatus={connectionStatus}
                notification={notification}
                setNotification={setNotification}
            />
            <FooterGT />
        </div>
    );
};

const HeaderGT = ({ connectionStatus }) => (
    <header className="bg-gradient-to-r from-blue-600 to-blue-800 text-white p-6 py-4">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
            <h1 className="text-xl font-bold">Helpdesk AI Assistant</h1>
            <div className="flex items-center justify-between gap-4 mt-2 sm:mt-0">
                <ConnectionStatus status={connectionStatus} />
                <div className="text-sm space-x-2">
                    <span>
                        Powered by
                        <a href="https://github.com/langchain4j/langchain4j" target="_blank" className="underline pl-1">
                            LangChain4j
                        </a>
                    </span>
                </div>
            </div>
        </div>
    </header>
);

const ChatWindowGT = React.memo(
    ({ messages, isLoading, sendMessage, input, setInput, inputRef, messagesEndRef, connectionStatus }) => (
        <div className="p-6 h-[calc(100vh-240px)] min-h-[300px] flex flex-col">
            <div className="bg-gray-50 rounded-lg border border-gray-200 shadow-inner overflow-hidden flex-1">
                <div className="h-full overflow-y-auto p-4 space-y-4" style={{ scrollBehavior: 'smooth' }}>
                    {messages.length === 0 && (
                        <div className="text-center text-gray-500 mt-4">
                            Start a conversation by sending a message.
                        </div>
                    )}
                    {messages.map((msg, index) => (
                        <div key={index} className={`flex ${msg.isUser ? 'justify-end' : 'justify-start'}`}>
                            <div
                                className={`max-w-xs lg:max-w-md px-4 py-2 rounded-lg shadow-md ${
                                    msg.isUser
                                        ? 'bg-gradient-to-r from-blue-500 to-blue-600 text-white ml-auto'
                                        : 'bg-white border border-gray-200'
                                } fade-in`}
                            >
                                {msg.text?.split("\n").map((line, index) => (
                                    <span key={index}>
                                        {line}
                                        <br />
                                    </span>
                                ))}
                            </div>
                        </div>
                    ))}
                    {isLoading && (
                        <div className="flex justify-start">
                            <div className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg typing-indicator">
                                AI is processing
                            </div>
                        </div>
                    )}
                    <div ref={messagesEndRef} />
                </div>
            </div>
            <form onSubmit={sendMessage} className="pt-6 bg-white rounded-b-lg">
                <div className="flex border border-gray-200 rounded-lg overflow-hidden transition-all duration-200">
                    <input
                        ref={inputRef}
                        type="text"
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        placeholder="Type your message..."
                        className="flex-grow px-4 py-2 focus:outline-none"
                        disabled={isLoading || connectionStatus !== 'connected'}
                    />
                    <button
                        type="submit"
                        className="bg-blue-500 text-white px-6 py-2 hover:bg-blue-600 disabled:opacity-50 transition-all duration-200 flex items-center"
                        disabled={isLoading || connectionStatus !== 'connected'}
                    >
                        <span>{isLoading ? 'Sending...' : 'Send'}</span>
                        <i data-lucide={isLoading ? 'loader' : 'send'} className="w-4 h-4 ml-2"></i>
                    </button>
                </div>
            </form>
        </div>
    )
);

const ActionsGT = ({ resetChat, toggleConnection, connectionStatus, notification, setNotification }) => (
    <div className="p-6 pt-0 text-center relative">
        <div className="flex justify-center space-x-4">
            <button
                onClick={toggleConnection}
                className={`bg-gradient-to-r ${
                    connectionStatus === 'connected' 
                    ? 'from-amber-100 to-amber-200 text-amber-600 hover:from-amber-400 hover:to-amber-600' 
                    : 'from-green-100 to-green-200 text-green-600 hover:from-green-400 hover:to-green-600'
                } hover:text-white px-6 py-2 rounded-lg 
                focus:outline-none focus:ring-2 focus:ring-offset-2
                transition-all duration-200 shadow-md flex items-center`}
            >
                <span className="text-sm">
                    {connectionStatus === 'connected' ? 'Disconnetti' : 'Connetti'}
                </span>
                <i data-lucide={connectionStatus === 'connected' ? 'plug-off' : 'plug'} className="w-4 h-4 ml-2"></i>
            </button>
            
            <button
                onClick={resetChat}
                className="bg-gradient-to-r from-red-100 to-red-200
                text-red-500 hover:text-white px-6 py-2 rounded-lg hover:from-red-400 hover:to-red-600
                focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2
                transition-all duration-200 shadow-md flex items-center"
            >
                <span className="text-sm">Clear Chat</span>
                <i data-lucide="trash-2" className="w-4 h-4 ml-2"></i>
            </button>
        </div>
        {notification && (
            <div className="absolute left-1/2 transform -translate-x-1/2 mt-2">
                <NotificationGT message={notification.message} type={notification.type} onClose={() => setNotification(null)} />
            </div>
        )}
    </div>
);

const FooterGT = () => (
    <footer className="bg-gray-100 p-6 py-4 text-center text-sm text-gray-600 border-t border-gray-200">
        <p className="flex items-center justify-center">
            <span>© 2025 Helpdesk AI Assistant</span>
        </p>
    </footer>
);

ReactDOM.render(<ChatGT />, document.getElementById('root'));

function generateID() {
    return window.crypto?.randomUUID?.() || Date.now().toString();
}