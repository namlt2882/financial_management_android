package project.baonq.service;

import java.io.BufferedReader;
import java.io.IOException;

public class BaseAuthService {

    protected String read(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
