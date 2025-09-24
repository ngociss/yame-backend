package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.model.Token;
import vn.yame.repository.TokenRepository;
import vn.yame.service.TokenService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;



    @Override
    public Long save(Token token) {
        Optional<Token> optionalToken = tokenRepository.findTokenByUsername(token.getUsername());
        if(optionalToken.isEmpty()) {
            tokenRepository.save(token);
            return token.getId();
        } else {
            Token currentToken = optionalToken.get();
            currentToken.setAccessToken(token.getAccessToken());
            currentToken.setRefreshToken(token.getRefreshToken());
            tokenRepository.save(currentToken);
            return currentToken.getId();
        }

    }

    @Override
    public String delete(Token token) {
        tokenRepository.delete(token);
        return "Delete token successfully";
    }

    public Token getByUsername (String username) {
        return tokenRepository.findTokenByUsername(username).orElseThrow(() -> new NotFoundResourcesException("Token not found"));
    }

}
