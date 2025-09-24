package vn.yame.service;

import vn.yame.model.Token;

public interface TokenService {

    public Long save(Token token);

    public String delete(Token token);

    public Token getByUsername (String username);

}
