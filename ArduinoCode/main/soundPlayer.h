enum AudioFeedback {
    INSTRUMENTAL = 0, VERBAL, JOKES, AUDIOBOOK, NONE
};

enum Tune {
  MILLION_DOLLAR = 0,
  LION_KING,
  OLE,

  NUM_OF_TUNES
};

class SoundPlayer
{
  private:
    void playNoaKirelMillionDollar(int piezoPin, int ledPin);
    void playLionKing(int piezoPin, int ledPin);
    void playOle(int piezoPin, int ledPin);

    void playInstrumental(int piezoPin, int ledPin);
  
  public:
    void playSuccessIndication(int piezoPin, int ledPin, AudioFeedback audioFeedback);
};
