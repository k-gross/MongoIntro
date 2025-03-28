// Query 4
// Find user pairs (A,B) that meet the following constraints:
// i) user A is male and user B is female
// ii) their Year_Of_Birth difference is less than year_diff
// iii) user A and B are not friends
// iv) user A and B are from the same hometown city
// The following is the schema for output pairs:
// [
//      [user_id1, user_id2],
//      [user_id1, user_id3],
//      [user_id4, user_id2],
//      ...
//  ]
// user_id is the field from the users collection. Do not use the _id field in users.
// Return an array of arrays.

function suggest_friends(year_diff, dbname) {
    db = db.getSiblingDB(dbname);

    let pairs = [];
    // TODO: implement suggest friends
    let males = db.users.find({gender: "male"}).toArray();
    let females = db.users.find({ gender: "female" }).toArray();

    males.forEach(male => {
        females.forEach(female => {
            let dif = male.YOB - female.YOB;
            if(Math.abs(dif) < year_diff && male.hometown.city == female.hometown.city
            && !male.friends.includes(female.user_id) && !female.friends.includes(male.user_id))
            {
                pairs.push([male.user_id, female.user_id]);
            }
        })
    })
    return pairs;
}
