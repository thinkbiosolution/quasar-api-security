const changeColor = () => {
  const color = document.getElementById('color-input').value;
  document.body.style.backgroundColor = color;
};

document.getElementById('color-button').addEventListener('click', changeColor);
