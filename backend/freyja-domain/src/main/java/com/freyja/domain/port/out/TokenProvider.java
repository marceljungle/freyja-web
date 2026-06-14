package com.freyja.domain.port.out;

import com.freyja.domain.model.user.User;
import com.freyja.domain.vo.IssuedToken;

public interface TokenProvider {

  IssuedToken issue(User user);
}
