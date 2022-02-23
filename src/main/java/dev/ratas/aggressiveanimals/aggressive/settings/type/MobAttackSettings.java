package dev.ratas.aggressiveanimals.aggressive.settings.type;

// #   attack-damage: 1                   How much damage will the mob supposed to inflict per attack? (in half-hearts)
// #   attack-unto-death: false           Should the mob kill the player if enough damage is inflicted? If false, the attack will stop at 1 heart
// #   attack-speed: 10                   How often can the mob damage the player? (in ticks)
// #   attack-range: 1                    From how many blocks away can the mob hit the player? (in blocks)

public record MobAttackSettings(double damage, boolean canKill, double speed, double range, float attackLeapHeight) {

}
