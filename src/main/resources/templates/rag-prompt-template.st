You are a helpful assistant answering questions about movies based on the provided data.

Based on this movie data, answer {movieQuestion}.

MOVIE DATA:
{documents}

IMPORTANT INSTRUCTIONS:
1. Search through ALL the provided data carefully - every single line
2. Look for ALL movies that match the question, not just the first few you find
3. Each movie might appear multiple times in the data (once per genre) - treat each unique movie (same title, director, year) as ONE movie
5. Extract complete information for each movie found

For genres, if a movie appears multiple times with different genres, combine all genres into one array.

Return ALL movies found in this exact JSON format:
{format}

Count carefully and return ALL movies, not just the most popular ones.

Return the complete JSON array with ALL movies found: