#include "headers/audio.h"

#define fifo_threshold 96 //Audio out fifo

/****************************************************************************************
 * For playing the audio contained in passed array
****************************************************************************************/
void play_audio(int audio[],int audio_len)
{
	int fifospace;
	int play = 1;
	int buffer_index = 0;
	
	volatile int *audio_ptr = (int *) AUDIO_BASE;			// Audio port address
	*(audio_ptr) = 0x8; // clear write FIFOs
	*(audio_ptr) = 0x0; // deactivate clearing
	fifospace = *(audio_ptr + 1); // read the audio port fifospace register
	
	while(play)
	{
		fifospace = *(audio_ptr + 1); // read the audio port fifospace register
		
		if ( ((fifospace & 0x00FF0000) >> 4) >= fifo_threshold) // check WSRC, for < 75% full
		{
			/* store data until the audio-out FIFO is full when the audio data is not finished*/
			while ( (fifospace & 0x00FF0000) && (buffer_index < audio_len) )
			{
				*(audio_ptr + 2) = audio[buffer_index]; //left data
				*(audio_ptr + 3) = audio[buffer_index]; //right data
				++buffer_index;
				fifospace = *(audio_ptr + 1); // read the audio port fifospace register
			}
			
			if(buffer_index == audio_len) // end of audio data, finish playing and return to main!
			{
				play = 0;
			}
		}
	}
}