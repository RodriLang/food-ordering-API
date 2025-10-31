package com.group_three.food_ordering.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Generador de nicknames amigables y únicos para invitados.
 * * Uso: String nombre = NicknameGenerator.generarNicknameInvitado();
 * Ejemplo: "InvitadoVeloz734", "InvitadoCurioso109"
 */
public class NicknameGenerator {

    private NicknameGenerator() {}

    private static final String[] ADJETIVES = {
            "Veloz", "Curioso", "Amable", "Feliz", "Valiente", "Sigiloso",
            "Explorador", "Creativo", "Divertido", "Rápido", "Astuto"
    };

    private static final String[] SUSTANTIVES = {
            "Armadillo", "Capibara", "Llama", "Zorro", "Gato", "Delfín",
            "Quetzal", "Jaguar", "Tapir", "Colibrí", "Puma", "Tucán"
    };

    public static String generateRandomNickname() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        String adjetive = ADJETIVES[random.nextInt(ADJETIVES.length)];
        String sustantive = SUSTANTIVES[random.nextInt(SUSTANTIVES.length)];
        int numero = random.nextInt(100);

        // Ej: "LlamaVeloz45"
        return sustantive + " " + adjetive + " " + numero;
    }
}
