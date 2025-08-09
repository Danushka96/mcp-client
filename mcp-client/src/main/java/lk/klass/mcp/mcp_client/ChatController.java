package lk.klass.mcp.mcp_client;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author danushka
 * 2025-08-09
 */
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatClient chatClient;
    private final ShoppingCart shoppingCart;

    @GetMapping(value = "/chat-stream", produces = "text/event-stream")
    private Flux<String> chat(@RequestParam String message) {
        record Acc(String out, String last) {
        }
        return chatClient
                .prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "007"))
                .tools(shoppingCart)
                .stream()
                .content()
                .scan(new Acc("", ""), (acc, tok) -> {
                    String out = acc.out();
                    String last = acc.last();
                    boolean isPunct = tok.matches("^[.,!?;:)\\]\\}].*");
                    boolean needSpace = !out.isEmpty() && !last.matches("\\s") && !isPunct && tok.matches("^[A-Za-z0-9].*");
                    String piece = (needSpace ? " " : "") + tok;
                    return new Acc(out + piece, piece.isEmpty() ? last : piece.substring(piece.length() - 1));
                })
                .skip(1)            // drop the seed
                .map(Acc::out)   // emit the built string so far
                .publishOn(Schedulers.boundedElastic());
    }
}
