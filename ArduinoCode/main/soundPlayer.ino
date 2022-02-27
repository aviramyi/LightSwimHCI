// a class responsible for playing different sounds, 
// has a library of sounds

#include "pitches.h"
#include "soundLibrary.h"


void SoundPlayer::playNoaKirelMillionDollar(int piezoPin, int ledPin) {
     // Play Melody - 'Noa Killer - Million Dollar' 
  for (int note = 0; note < MillionLen; note++){
    int curDur = 1400 / millionDollarNoteDurations[note];
    digitalWrite(ledPin, HIGH);
    tone(piezoPin, millionDollar[note], curDur);
    delay(int(curDur * 1.3));
    noTone(piezoPin);
    digitalWrite(ledPin, LOW);
    delay(100);
  }
}

void SoundPlayer::playOle(int piezoPin, int ledPin) {
     // Play Ole 
  for (int note = 0; note < OleLen; note++){
    int curDur = 600 / OleDuration[note];
    digitalWrite(ledPin, HIGH);
    tone(piezoPin, Ole[note], curDur);
    delay(int(curDur * 1.3));
    noTone(piezoPin);
    digitalWrite(ledPin, LOW);
    delay(100);
  }
}

void SoundPlayer::playLionKing(int piezoPin, int ledPin) {
     // Play Melody - 'Lion King - Can't Wait to Be King' 
  for (int note = 0; note < lionKingLen; note++){
    int curDur = 600 / lionKingNoteDuration[note];
    digitalWrite(ledPin, HIGH);
    tone(piezoPin, lionKing[note], curDur);
    delay(int(curDur * 1.3));
    noTone(piezoPin);
    digitalWrite(ledPin, LOW);
    delay(100);
  }
}

void blinkLed(int ledPin) {
  digitalWrite(ledPin, HIGH);
  delay(1000);
  digitalWrite(ledPin, LOW);
  delay(1000);
}

void SoundPlayer::playInstrumental(int piezoPin, int ledPin) {
  int tuneIndex = random(NUM_OF_TUNES);
      switch ((Tune)tuneIndex) {
        case MILLION_DOLLAR:
        {
          playNoaKirelMillionDollar(piezoPin, ledPin);
          break;
        }
        case LION_KING:
        {
          playLionKing(piezoPin, ledPin);
          break;
        }
        default:
        {
          playOle(piezoPin, ledPin);
          break;
        }
      }
}

void SoundPlayer::playSuccessIndication(int piezoPin, int ledPin, AudioFeedback audioFeedback) {
  switch (audioFeedback) {
    case INSTRUMENTAL:
    {
      playInstrumental(piezoPin, ledPin);
      break;
    }
    default:
    {
      blinkLed(ledPin);
      return;
    }
  }
}
