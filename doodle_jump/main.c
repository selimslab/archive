#include "headers/address_map_arm.h"

#include "headers/data.h"
#include "vga.c"
#include "audio.c"
#include "interrupt_related.c"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#define text_x 68
#define score_x (text_x+7)
#define score_y 1

#define green 0x3F03
#define yellow 0xFFE0
#define red 0xF800
#define brown 0x5140
#define blue 0x393B


int g=1;
int box_size = 7;
void play_audio(int[],int);
int bufferPixels[76800];

void keep_score(){
	int score = 0; 
	int health = 100;
}




void draw_box(struct box b){
	if(!b.is_invisible)
		VGA_box (b.x, b.y, b.x + b.width, b.y+b.height, b.color); 
}




void draw(int image[]){
	int i, j,offset=0;
	int pixel[arr_size(image)];
	for (i=0; i < 320; i++)
		for (j=0; j < 240; j++)
		{
			pixel[offset] = image[offset];
			if (pixel[offset] != 0 & pixel[offset] != 65535)
			{write_pixel(i, j, pixel[offset]);}
			offset++;
		}
}




void delete_box(struct box b){
	recover_background(b.x, b.x + b.width, b.y, b.y+b.height);
}

void delete_hero() 
{
	recover_background(osman.x,osman.x + osman.width-1, osman.y, osman.y + osman.height-1);
}



void init_hero(){
	
	osman.x = 150;
    osman.y = 30;
    osman.alive = 1;
    osman.speed_x = 0;
    osman.speed_y = 0;
    osman.acceleration = 4;
    osman.max_speed = 20;
    osman.width = 28;
    osman.height = 30;
	osman.score = 0;
};



void draw_boxes(){
	int i;
	for(i=0;i<box_size;i++){
		int x2 = boxes[i].x+boxes[i].width;
		int y2 = boxes[i].y+boxes[i].height;
		if(!boxes[i].is_invisible)
			VGA_box(boxes[i].x, boxes[i].y, x2, y2, boxes[i].color );

	}
}

void create_boxes(){
	
	int i;
    for (i = 0; i < box_size; i++) {
      struct box new_box;
      new_box.width = 50;
      new_box.height = 5;
	  if(i==5){
		new_box.x = 130;
		}
		else  {
			boxes[i].x = rand() % (vga_width - boxes[i].width);
		}
		new_box.y = i*30;
	  new_box.is_broken = 0;
	  new_box.is_moving = 0;
	  new_box.direction = 1;
	  new_box.is_invisible = 0;
      new_box.color = green;
      boxes[i] = new_box;
    }

}


void box_fall(int disposition){
	osman.score += disposition;
	int i;
	   for (i = 0; i < box_size; i++)
	  { 
		delete_box(boxes[i]);

		boxes[i].y = boxes[i].y + disposition;
		if (boxes[i].y >= vga_height) 
		{
		  boxes[i].y = 0;
		  boxes[i].x = rand() % (vga_width - boxes[i].width);
		  boxes[i].is_invisible = 0;
		  int broken = rand() % 100;
		  if(broken<73){
			  boxes[i].is_broken = 0;
			  boxes[i].is_moving = 0;
			  boxes[i].is_invisible = 0;
			  boxes[i].color = green;	  
			}
			else if(broken<92){
				boxes[i].is_broken = 0;
			  boxes[i].is_moving = 1;
			  boxes[i].is_invisible = 0;
			  boxes[i].color = blue;	 
			}
			else {
				boxes[i].is_broken = 1;
				boxes[i].is_moving = 0;
			  boxes[i].is_invisible = 0;
			  boxes[i].color = brown;
			}
		}
		draw_box(boxes[i]);
	  }
}

int update_hero_position(){
	  osman.y = osman.y + osman.speed_y/10;
	  
	  if (osman.x > vga_width) 
	  {
		 osman.x = 0;
	  }
	  
	  if (osman.x < 0) 
	  {
		 osman.x = 320 - osman.width;
	  }
	  
	  osman.speed_y = osman.speed_y + osman.acceleration;
	  
	  if (osman.speed_y > osman.max_speed) 
	  {
		osman.speed_y = osman.max_speed;
	  }
	  
	  if (osman.y > vga_height) 
	  {
		return 0;
		osman.y = vga_height/3;
	  }
	  
	  if (osman.y < vga_height/3) 
	  {
		int tmp = osman.y;
		osman.y = vga_height/3;
		box_fall(vga_height/3 - tmp +1);
	  }

	  return 1;
}



void jump_if_necessary(){
		
	if(osman.speed_y<0)
		return;
	
      bool shouldJump = 0;
	  int i;
	  for (i = 0; i < box_size; i++) //jump
	  { 
		bool betweenX = (osman.x >= boxes[i].x && osman.x < boxes[i].x + boxes[i].width);
		bool betweenX2 = (osman.x + osman.width >= boxes[i].x && osman.x + osman.width < boxes[i].x + boxes[i].width);
		bool betweenY = (osman.y + osman.height >= boxes[i].y && osman.y + osman.height < boxes[i].y + boxes[i].height);
		bool curShouldJump = ((betweenX | betweenX2) & betweenY);
		if(curShouldJump & boxes[i].is_broken){
			delete_box(boxes[i]);
			boxes[i].is_invisible = 1;
		}
		shouldJump = shouldJump | ((betweenX | betweenX2) & betweenY & (!boxes[i].is_broken));
	  }

	  if (shouldJump) 
	  {
		osman.speed_y = -100;
	  }
}


void update_box_positions(){
	int i;
	for (i = 0; i < box_size; i++) //jump
	  { 
		if(boxes[i].is_moving){
			delete_box(boxes[i]);
			boxes[i].x += boxes[i].direction;
		}
		
		if(boxes[i].x+boxes[i].width >= vga_width){
			boxes[i].x = vga_width - boxes[i].width;
			boxes[i].direction*=-1;
		}
		
		if(boxes[i].x <= 0){
			boxes[i].x = 0;
			boxes[i].direction*=-1;
		}
	  }
}


void do_config(){
	set_A9_IRQ_stack();            // initialize the stack pointer for IRQ mode
	config_GIC();                  // configure the general interrupt controller
	config_PS2();
	enable_A9_interrupts(); // enable interrupts
}

void clear_screen(){
	 VGA_box(0, 0, vga_width, vga_height, 0); 
}


void display_score(){
	 /* Score message to be displayed on the VGA screen */
	char text[10] = "SCORE:\0";
	char score[16];
	sprintf(score, "%d", osman.score);

	VGA_text(text_x, score_y, text);
	VGA_text(score_x, score_y, score);
}



int play(){
	 
	 delete_hero(); //hero fall
	 
	  update_box_positions();
		  
	  draw_boxes();
	    
	  int alive = update_hero_position();
		  
	  jump_if_necessary();
	  
	  draw_hero(osman, hero_matrix);
	  
	  display_score();
	  

	return alive;
}

void game_over_func(){
    /* Score message to be displayed on the VGA screen */
	clear_screen();
	draw(game_over);
	play_audio(audio_death, arr_size(audio_death)); // play death music
}

int main(void)
{	
	do_config();
	
	while(1){
	start_game=0;
	
	osman.score = 0;
	
	clear_screen();
	
	init_hero();
	
	draw(background);

    create_boxes();

    draw_boxes();

    draw_hero(osman, hero_matrix);
	
	play_audio(start_buffer, arr_size(start_buffer)); // play start music
	
	int alive = 1;
	
    while(alive) 
	{
	
		int k=0;
		while(k<11999999){
			k++;
		}
			
		alive = play();

		if(!alive){	
			game_over_func();
			long long int a=0;
			while(a<2000000000){
			a++;
		}
		}
		
    }
	
	}
    return 0;
	
}