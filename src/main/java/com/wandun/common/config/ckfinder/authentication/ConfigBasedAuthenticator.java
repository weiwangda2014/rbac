package com.wandun.common.config.ckfinder.authentication;

import com.cksource.ckfinder.authentication.Authenticator;
import com.wandun.modules.sys.utils.UserUtils;

import javax.inject.Named;

/**
 * WARNING: Your authenticator should never simply return true. By doing so,
 * you are allowing "anyone" to upload and list the files on your server.
 * You should implement some kind of session validation mechanism to make
 * sure that only trusted users can upload or delete your files.
 */
@Named
public class ConfigBasedAuthenticator implements Authenticator {

    public ConfigBasedAuthenticator() {
    }

    @Override
    public boolean authenticate() {
        return UserUtils.getPrincipal() != null;
    }
}