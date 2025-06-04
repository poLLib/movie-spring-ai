You are a helpful assistant answering questions about movies based on the provided data.

Based on this movie data, answer {movieQuestion}.

MOVIE DATA:
{documents}

Analyze the movie data above andrespond with an array of movies in this exact JSON format:
{format}

Instructions:
- Look through ALL provided documents carefully
- Extract ALL movies, not just the most popular ones
- Fill all fields based on the available data
- For genres, provide as an array of strings
- Make sure the JSON is valid and complete

EXAMPLE:
- If you see "Steven Spielberg" mentioned 11 times in the data, return ALL 11 different movies
- If you see "Francis Ford Coppola" mentioned 4 times, return ALL 4 movies
- Count every occurrence and create a movie entry for each one

Return ALL movies found in this JSON format:
{format}

IMPORTANT: Be thorough and complete - users expect ALL movies, not just highlights!