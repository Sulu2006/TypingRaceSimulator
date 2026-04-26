class Main
{
    public static void main(String[] args)
    {
        TypingRace race = new TypingRace(40);

        Typist typist1 = new Typist('①', "TURBOFINGERS", 0.85);
        Typist typist2 = new Typist('②', "QWERTY_QUEEN", 0.60);
        Typist typist3 = new Typist('③', "HUNT_N_PECK", 0.30);

        race.addTypist(typist1, 1);
        race.addTypist(typist2, 2);
        race.addTypist(typist3, 3);

        race.startRace();
    }
}
