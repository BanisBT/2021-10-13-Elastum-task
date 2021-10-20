package com.tbarauskas.elastumtask.model;

public enum Kinship {

    HUSBAND,
    WIFE,
    SON,
    DAUGHTER,
    FATHER,
    MOTHER,
    BROTHER,
    SISTER,
    GRANDFATHER,
    GRANDMOTHER,
    GRANDSON,
    GRANDDAUGHTER;

    public boolean isSameGeneration() {
        return this.equals(HUSBAND) | this.equals(WIFE) | this.equals(BROTHER) | this.equals(SISTER);
    }

    public boolean isNextGeneration() {
        return this.equals(DAUGHTER) | this.equals(SON);
    }

    public boolean isGrandNextGeneration() {
        return this.equals(GRANDDAUGHTER) | this.equals(GRANDSON);
    }

    public boolean isPreviousGeneration() {
        return this.equals(MOTHER) | this.equals(FATHER);
    }

    public boolean isGrandPreviousGeneration() {
        return this.equals(GRANDMOTHER) | this.equals(GRANDFATHER);
    }

    public boolean isMale() {
        return this.equals(HUSBAND) | this.equals(BROTHER) | this.equals(FATHER) | this.equals(SON)
                | this.equals(GRANDSON) | this.equals(GRANDFATHER);
    }

    public boolean isFemaleWithHusbandSurname() {
        return this.equals(WIFE) | this.equals(MOTHER) | this.equals(GRANDMOTHER);
    }

    public boolean isFemaleWIthFamilySurname() {
        return this.equals(SISTER) | this.equals(DAUGHTER) | this.equals(GRANDDAUGHTER);
    }
}
