
Parse.Cloud.beforeSave('GameScore', function(request, response) {
  if (request.object.has('playerName')) {
    response.success();
  } else {
    response.error('Each GameScore must have a playerName');
  }
});
