###### GET THE TEXT PRINTED AT THE CONSOLE AND GO TO https://www.wordle.net/  :)
import random

frequencies = [851, 519, 495, 476, 451, 387, 352, 350, 335, 327, 323, 311, 306, 286, 274, 271, 258, 250, 249, 235, 225, 219, 209, 209, 202, 201, 200, 197, 196, 194, 192, 185, 181, 181, 180, 179, 179, 169, 168, 166, 165, 159, 158, 156, 156, 155, 154, 153, 152, 147, 146, 146, 145, 145, 144, 142, 142, 139, 138, 137, 137, 135, 135, 134, 133, 129, 129, 125, 121, 121, 120, 117, 116, 114, 114, 114, 113, 112, 111, 111, 110, 108, 108, 108, 108, 108, 107, 106, 106, 106, 106, 105, 104, 103, 102, 101, 101, 101, 101, 100]
words = ['love', 'night', 'story', 'life', 'last', 'girl', 'black', 'time', 'little', 'dead', 'death', 'house', 'world', 'movie', 'christmas', 'blood', 'dark', 'city', 'american', 'white', 'your', 'king', 'lost', 'days', 'secret', 'that', 'blue', 'woman', 'good', 'three', 'wild', 'summer', 'great', 'home', 'girls', 'don', 'live', 'lady', 'island', 'high', 'this', 'road', 'back', 'what', 'kill', 'street', 'moon', 'first', 'about', 'the', 'hell', 'legend', 'killer', 'heart', 'return', 'when', 'like', 'young', 'ghost', 'family', 'star', 'women', 'part', 'boys', 'game', 'murder', 'land', 'fire', 'adventures', 'people', 'under', 'after', 'evil', 'down', 'journey', 'never', 'beyond', 'devil', 'perfect', 'living', 'long', 'space', 'monster', 'wedding', 'school', 'best', 'year', 'battle', 'final', 'baby', 'come', 'angel', 'eyes', 'happy', 'before', 'earth', 'without', 'dragon', 'dream', 'seven']

frequencies = [x//4 for x in frequencies]
final_text = ""
for word, frequency in zip(words, frequencies):
    for i in range(frequency):
        final_text += word + " "

final_text = final_text.split(" ")
random.shuffle(final_text)
text = ""
for word in final_text:
    text += word + " "

print(text)