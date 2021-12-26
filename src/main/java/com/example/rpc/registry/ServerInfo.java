package com.example.rpc.registry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LAJ
 * @date 2021-12-25 20:36:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerInfo {
    private String host;
    private int port;
}
