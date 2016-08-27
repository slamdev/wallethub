SELECT
  v.name,
  v.votes,
  COUNT(vj.votes) + 1 AS rank
FROM
  votes v
  LEFT JOIN
  votes vj ON v.votes > vj.votes
GROUP BY v.name, v.votes
ORDER BY rank;
