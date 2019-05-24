function init() {
}

function log(error) {
  console.log(error);
  // Raven.captureException(error);
}

export default {
  init,
  log,
};
